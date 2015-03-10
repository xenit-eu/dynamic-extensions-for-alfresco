package com.github.dynamicextensionsalfresco.controlpanel;

import aQute.bnd.osgi.Analyzer;
import com.github.dynamicextensionsalfresco.event.EventListener;
import com.github.dynamicextensionsalfresco.event.events.SpringContextException;
import com.github.dynamicextensionsalfresco.osgi.ManifestUtils;
import com.github.dynamicextensionsalfresco.osgi.RepositoryStoreService;
import com.springsource.util.osgi.manifest.BundleManifest;
import com.springsource.util.osgi.manifest.BundleManifestFactory;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.PackageAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.webscripts.DeclarativeRegistry;
import org.springframework.extensions.webscripts.Registry;
import org.springframework.extensions.webscripts.servlet.FormData.FormField;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper for working with {@link Bundle}s.
 * 
 * @author Laurens Fridael
 * 
 */
@Component
public class BundleHelper implements EventListener<SpringContextException>, FrameworkListener {
    private final static Logger logger = LoggerFactory.getLogger(BundleHelper.class);

	private static final String ALFRESCO_DYNAMIC_EXTENSION_HEADER = "Alfresco-Dynamic-Extension";

	/**
	 * Tests if the given bundle contains a Dynamic Extension.
	 * <p>
	 * This implementation looks if the bundle header <code>Alfresco-Dynamic-Extension</code> equals the String "true".
	 */
	public static boolean isDynamicExtension(final Bundle bundle) {
		return "true".equals(bundle.getHeaders().get(ALFRESCO_DYNAMIC_EXTENSION_HEADER));
	}

	/* Dependencies */

	@Autowired
	BundleContext bundleContext;

	@Autowired
	RepositoryStoreService repositoryStoreService;

	@Autowired
	FileFolderService fileFolderService;

	@Autowired
	ContentService contentService;

	@Autowired
	NodeService nodeService;

	@Autowired @Resource(name = "webscripts.container")
	org.springframework.extensions.webscripts.Container webScriptsContainer;

	private final Queue<Bundle> bundlesToStart = new ConcurrentLinkedQueue<Bundle>();

	private final BlockingQueue<InstallResult> installResults = new LinkedBlockingDeque<InstallResult>();

	/* Main operations */

    @PostConstruct
    void registerEventListeners() throws Exception {
	    bundleContext.registerService(EventListener.class, this, null);

	    bundleContext.addFrameworkListener(this);
    }

    /**
	 * Obtains the {@link Bundle}s that comprise the core framework.
	 */
	public List<Bundle> getFrameworkBundles() {
		final List<Bundle> bundles = new ArrayList<Bundle>();
		for (final Bundle bundle : bundleContext.getBundles()) {
			if (isDynamicExtension(bundle) == false) {
				bundles.add(bundle);
			}
		}
		return bundles;
	}

	/**
	 * Obtains the {@link Bundle}s that comprise the core framework.
	 */
	public List<Bundle> getExtensionBundles() {
		final List<Bundle> bundles = new ArrayList<Bundle>();
		for (final Bundle bundle : bundleContext.getBundles()) {
			if (isDynamicExtension(bundle)) {
				bundles.add(bundle);
			}
		}
		return bundles;
	}

	/**
	 * Obtains the {@link Bundle} for the given id.
	 * 
	 * @param id BundleId
	 * @return The matching {@link Bundle} or null if no match could be found.
	 */
	public Bundle getBundle(final long id) {
		return bundleContext.getBundle(id);
	}

	/**
	 * Installs an uploaded file as a bundle in the repository.
	 * <p>
	 * This implementation first saves the upload to a temporary file. It then attempts to install the file as a bundle.
	 * If this succeeds, it saves the bundle in the repository.
	 * 
	 * @param file form field
	 * @return installed Bundle
	 * @throws IOException
	 * @throws BundleException
	 */
	public Bundle installBundleInRepository(final FormField file) throws IOException, BundleException {
		final File tempFile = saveToTempFile(file.getInputStream());
        return doInstallBundleInRepository(tempFile, file.getFilename());
	}

	/**
	 * Installs a bundle using the given {@link Content} and filename.
	 * 
	 * @param content uploaded content
	 * @return installted Bundle
	 * @throws IOException
	 * @throws BundleException
	 */
	public Bundle installBundleInRepository(final Content content) throws IOException, BundleException {
		final File tempFile = saveToTempFile(content.getInputStream());
		return doInstallBundleInRepository(tempFile, null);

	}

	public NodeRef uninstallAndDeleteBundle(final Bundle bundle) throws BundleException {
        NodeRef matchingNode = null;
		final Matcher matcher = Pattern.compile("/Company Home(/.+)+/(.+\\.jar)$").matcher(bundle.getLocation());
		if (matcher.matches()) {
			final String filename = matcher.group(2);
			final NodeRef bundleFolder = repositoryStoreService.getBundleFolder(false);
			if (bundleFolder != null) {
				final NodeRef file = fileFolderService.searchSimple(bundleFolder, filename);
				if (file != null) {
					final Map<QName, Serializable> properties = Collections.emptyMap();
					nodeService.addAspect(file, ContentModel.ASPECT_TEMPORARY, properties);
					nodeService.deleteNode(file);
                    matchingNode = file;
                    bundle.uninstall();
				}
			}
		}

        return matchingNode;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<ServiceReference> getAllServices() {
		try {
			return (List) Arrays.asList(bundleContext.getAllServiceReferences(null, null));
		} catch (final InvalidSyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	protected <T> T getService(final Class<T> service) {
		final ServiceReference<T> serviceReference = bundleContext.getServiceReference(service);
		if (serviceReference != null) {
			return bundleContext.getService(serviceReference);
		} else {
			return null;
		}
	}

	/* Utility operations */

	protected Bundle doInstallBundleInRepository(File tempFile, String fileName) throws
        BundleException, IOException {

		installResults.clear();

		try {
			BundleIdentifier identifier = getBundleIdentifier(tempFile);
			if (identifier == null) {
                tempFile = wrapPlainJar(tempFile, fileName);
                if (tempFile != null) {
                    identifier = getBundleIdentifier(tempFile);
                }
                if (identifier == null) {
                    throw new BundleException(
                        "Could not generate Bundle filename. Make sure the content is an OSGi bundle.");
                }
                logger.info("Wrapped plain jar as a OSGi bundle: {}.", identifier.getSymbolicName());
			}
            final String filename = identifier.toJarFilename();
            final String location = generateRepositoryLocation(filename);
			Bundle bundle = bundleContext.getBundle(location);
            boolean classpathBundle = false;
            if (bundle == null) {
                bundle = findBundleBySymbolicName(identifier);
                if (bundle != null) {
                    final NodeRef deletedNode = uninstallAndDeleteBundle(bundle);
                    if (deletedNode != null) {
                        logger.warn(
                            "Deleted existing repository bundle {} with an identical Symbolic name: {}.",
                            deletedNode, identifier.getSymbolicName()
                        );
                        bundle = null;
                    } else {
                        classpathBundle = true;
                    }
                }
            }

			final InputStream in = createStreamForFile(tempFile);
			if (bundle != null) {
                // we stop and delay restarting the bundle, as otherwise, the refresh would cause 2 immediate restarts,
                // this in turn causes havoc upon the asynchronous Spring integration startup
                bundle.stop();
				bundle.update(in);

                @SuppressWarnings("deprecation") final PackageAdmin packageAdmin = getPackageAdmin();
                final Bundle[] bundleSet = {bundle};

                // resolve to synchronously assert dependencies are in order
                packageAdmin.resolveBundles(bundleSet);

                if (isFragmentBundle(bundle) == false) {
                    bundlesToStart.offer(bundle);
                    // async operation
                    packageAdmin.refreshPackages(bundleSet);
                } else {
	                return bundle;
                }
            } else {
				bundle = bundleContext.installBundle(location, in);
                if (isFragmentBundle(bundle) == false) {
                    bundle.start();
                    installResults.add(new InstallResult(null));
                }
			}
            if (!classpathBundle) {
                final BundleManifest manifest = createBundleManifest(bundle);
                saveBundleInRepository(tempFile, filename, manifest);
            } else {
                logger.warn("Temporarily updated classpath bundle: {}, update will be reverted after restart.", bundle.getSymbolicName());
            }

			try {
				evaluateInstallationResult(installResults.poll(15, TimeUnit.SECONDS));
			} catch (InterruptedException tx) {
                logger.warn("Timed out waiting for an installation result", tx);
            }

			resetWebScriptsCache();

			return bundle;
		} finally {
            if (tempFile != null) {
                tempFile.delete();
            }
        }
	}

    protected BundleManifest createBundleManifest(Bundle bundle) {
        return BundleManifestFactory.createBundleManifest(bundle.getHeaders());
    }

    protected InputStream createStreamForFile(File file) throws FileNotFoundException {
        return new FileInputStream(file);
    }

    private static void evaluateInstallationResult(InstallResult installResult) throws BundleException {
        if (installResult != null) {
            if (installResult.exception instanceof RuntimeException) {
                throw (RuntimeException)installResult.exception;
            } else if (installResult.exception instanceof BundleException) {
                throw (BundleException)installResult.exception;
            }
        }
    }

	@SuppressWarnings("deprecation")
    protected PackageAdmin getPackageAdmin() {
        return bundleContext.getService(bundleContext.getServiceReference(PackageAdmin.class));
    }

    private File wrapPlainJar(File tempFile, String fileName) {
        try {
            JarFile jar = new JarFile(tempFile);

            final Analyzer analyzer = new Analyzer();
            final String manifestVersion = ManifestUtils.getImplementationVersion(jar);
            if (manifestVersion != null) {
                analyzer.setBundleVersion(manifestVersion);
            }
            String name = ManifestUtils.getImplementationTitle(jar);
            if (name == null) {
                if (fileName == null) {
                    return null;
                } else {
                    name = fileName.replaceFirst("^(.+)\\.\\w+$", "$1");
                }
            }
            analyzer.setBundleSymbolicName(name);

            analyzer.setJar(tempFile);
            analyzer.setImportPackage("*;resolution:=optional");
            analyzer.setExportPackage("*");
            analyzer.analyze();
            final Manifest manifest = analyzer.calcManifest();
            analyzer.getJar().setManifest(manifest);
            final File wrappedTempFile = File.createTempFile("wrapped", ".jar");
            analyzer.save(wrappedTempFile, true);
            return wrappedTempFile;
        } catch (Exception e) {
            logger.warn(String.format("Failed to wrap plain %s jar using bnd.", tempFile), e);
            return null;
        }
    }


    protected Bundle findBundleBySymbolicName(BundleIdentifier identifier) {
        final Bundle[] allBundles = bundleContext.getBundles();
        for (Bundle aBundle : allBundles) {
            if (aBundle.getSymbolicName().equals(identifier.getSymbolicName())) {
                return aBundle;
            }
        }
        return null;
    }

    protected File saveToTempFile(final InputStream data) throws IOException {
		final File tempFile = File.createTempFile("dynamic-extensions-bundle", null);
		tempFile.deleteOnExit();
		FileCopyUtils.copy(data, new FileOutputStream(tempFile));
		return tempFile;
	}

	protected BundleIdentifier getBundleIdentifier(final File tempFile) throws IOException {
		BundleIdentifier identifier = null;
		final JarFile jarFile = new JarFile(tempFile);
		try {
			final Manifest manifest = jarFile.getManifest();
			final Attributes attributes = manifest.getMainAttributes();
			final String symbolicName = attributes.getValue(Constants.BUNDLE_SYMBOLICNAME);
			final String version = attributes.getValue(Constants.BUNDLE_VERSION);
			if (StringUtils.hasText(symbolicName) && StringUtils.hasText(version)) {
				identifier = BundleIdentifier.fromSymbolicNameAndVersion(symbolicName, version);
			}
			return identifier;
		} finally {
			jarFile.close();
		}
	}

	protected void saveBundleInRepository(final File file, final String filename, final BundleManifest manifest)
			throws IOException {
		final NodeRef bundleFolder = repositoryStoreService.getBundleFolder(true);
		NodeRef nodeRef = fileFolderService.searchSimple(bundleFolder, filename);
		if (nodeRef == null) {
			nodeRef = fileFolderService.create(bundleFolder, filename, ContentModel.TYPE_CONTENT).getNodeRef();
		}
		final String title = String.format("%s %s", manifest.getBundleName(), manifest.getBundleVersion());
		nodeService.setProperty(nodeRef, ContentModel.PROP_TITLE, title);
		nodeService.setProperty(nodeRef, ContentModel.PROP_DESCRIPTION, manifest.getBundleDescription());
		final ContentWriter writer = contentService.getWriter(nodeRef, ContentModel.PROP_CONTENT, true);
		writer.setMimetype(MimetypeMap.MIMETYPE_ZIP);
		writer.putContent(createStreamForFile(file));
	}

	protected String generateRepositoryLocation(final String filename) {
		return String.format("%s/%s", getBundleRepositoryLocation(), filename);
	}

	protected boolean isFragmentBundle(final Bundle bundle) {
		return bundle.getHeaders().get(Constants.FRAGMENT_HOST) != null;
	}

	/**
	 * The DeclarativeRegistry caches 404 results, which can hide new webscript deployments.
	 * Unfortunately there is no public API for resetting this cache.
	 */
	protected void resetWebScriptsCache() {
		final Registry registry = webScriptsContainer.getRegistry();
		if (registry instanceof DeclarativeRegistry) {
			try {
				final Field cacheField = DeclarativeRegistry.class.getDeclaredField("uriIndexCache");
				if (!cacheField.isAccessible()) {
					cacheField.setAccessible(true);
				}
				final Map cache = (Map) cacheField.get(registry);
				cache.clear();
			} catch (Exception e) {
				logger.error("failed to reset webscript cache", e);
			}
		}
	}

    @Override
    public void onEvent(SpringContextException event) {
        installResults.add(new InstallResult(event.getException()));
    }

    @Override
    public void frameworkEvent(FrameworkEvent event) {
        if (event.getType() == FrameworkEvent.PACKAGES_REFRESHED) {
            // start any bundles that were recently updated after the PackageAdmin has refreshed (restarted) any dependencies
            Bundle bundle;
            while ((bundle = bundlesToStart.poll()) != null) {
                try {
                    bundle.start();
                    installResults.add(new InstallResult(null));
                } catch (BundleException bx) {
                    installResults.add(new InstallResult(bx));
                }
            }
        }
    }

    /* Container */

	public String getBundleRepositoryLocation() {
		return repositoryStoreService.getBundleRepositoryLocation();
	}

	private static class InstallResult {
		public final Exception exception;

		public InstallResult(Exception runtimeException) {
			this.exception = runtimeException;
		}
	}
}
