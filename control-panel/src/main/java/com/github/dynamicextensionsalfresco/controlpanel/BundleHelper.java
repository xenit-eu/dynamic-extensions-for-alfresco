package com.github.dynamicextensionsalfresco.controlpanel;

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
import org.osgi.framework.*;
import org.osgi.framework.launch.Framework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.webscripts.servlet.FormData.FormField;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.*;
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
public class BundleHelper {
    private final static Logger logger = LoggerFactory.getLogger(BundleHelper.class);

	private static final String ALFRESCO_DYNAMIC_EXTENSION_HEADER = "Alfresco-Dynamic-Extension";

	/**
	 * Tests if the given bundle contains a Dynamic Extension.
	 * <p>
	 * This implementation looks if the bundle header <code>Alfresco-Dynamic-Extension</code> equals the String "true".
	 * 
	 * @param bundle
	 * @return
	 */
	public static boolean isDynamicExtension(final Bundle bundle) {
		return "true".equals(bundle.getHeaders().get(ALFRESCO_DYNAMIC_EXTENSION_HEADER));
	}

	/* Dependencies */

	@Autowired
	private BundleContext bundleContext;

	@Autowired
	private RepositoryStoreService repositoryStoreService;

	@Autowired
	private FileFolderService fileFolderService;

	@Autowired
	private ContentService contentService;

	@Autowired
	private NodeService nodeService;

	/* Container */

	/* Main operations */

	/**
	 * Obtains the {@link Bundle}s that comprise the core framework.
	 * 
	 * @return
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
	 * 
	 * @return
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
	 * @param id
	 * @return The matching {@link Bundle} or null if no match could be found.
	 */
	public Bundle getBundle(final long id) {
		return bundleContext.getBundle(id);
	}

	/**
	 * Obtains the {@link Framework} bundle.
	 * 
	 * @return
	 */
	public Framework getFramework() {
		return (Framework) bundleContext.getBundle(0);
	}

	/**
	 * Installs an uploaded file as a bundle in the repository.
	 * <p>
	 * This implementation first saves the upload to a temporary file. It then attempts to install the file as a bundle.
	 * If this succeeds, it saves the bundle in the repository.
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws BundleException
	 */
	public Bundle installBundleInRepository(final FormField file) throws IOException, BundleException {
		final File tempFile = saveToTempFile(file.getInputStream());
		return doInstallBundleInRepository(tempFile);
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
		return doInstallBundleInRepository(tempFile);

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
					final Map<QName, Serializable> properties = Collections.<QName, Serializable> emptyMap();
					nodeService.addAspect(file, ContentModel.ASPECT_TEMPORARY, properties);
					nodeService.deleteNode(file);
                    matchingNode = file;
				}
			}
		}
		bundle.uninstall();

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

	protected Bundle doInstallBundleInRepository(final File tempFile) throws
        BundleException, IOException {
		try {
			final BundleIdentifier identifier = getBundleIdentifier(tempFile);
			if (identifier == null) {
				throw new BundleException(
						"Could not generate Bundle filename. Make sure the content is an OSGi bundle.");
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
                    } else {
                        classpathBundle = true;
                    }
                }
            }
			final FileInputStream in = new FileInputStream(tempFile);
			if (bundle != null) {
				bundle.update(in);
			} else {
				bundle = bundleContext.installBundle(location, in);
			}
			if (isFragmentBundle(bundle) == false) {
				bundle.start();
			}
            if (!classpathBundle) {
                final BundleManifest manifest = BundleManifestFactory.createBundleManifest(bundle.getHeaders());
                saveBundleInRepository(tempFile, filename, manifest);
            } else {
                logger.warn("Temporarily updated classpath bundle: {}, update will be reverted after restart.", bundle.getSymbolicName());
            }
            return bundle;
		} finally {
			tempFile.delete();
		}
	}

    private Bundle findBundleBySymbolicName(BundleIdentifier identifier) {
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
		writer.putContent(new FileInputStream(file));
	}

	protected String generateRepositoryLocation(final String filename) {
		return String.format("%s/%s", getBundleRepositoryLocation(), filename);
	}

	protected boolean isFragmentBundle(final Bundle bundle) {
		return bundle.getHeaders().get(Constants.FRAGMENT_HOST) != null;
	}

	/* Container */

	public String getBundleRepositoryLocation() {
		return repositoryStoreService.getBundleRepositoryLocation();
	}

}
