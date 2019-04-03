package com.github.dynamicextensionsalfresco.controlpanel;

import aQute.bnd.osgi.Analyzer;
import com.github.dynamicextensionsalfresco.event.EventListener;
import com.github.dynamicextensionsalfresco.event.events.SpringContextException;
import com.github.dynamicextensionsalfresco.osgi.BundleDependencies;
import com.github.dynamicextensionsalfresco.osgi.ManifestUtils;
import com.github.dynamicextensionsalfresco.osgi.RepositoryStoreService;
import com.springsource.util.osgi.manifest.BundleManifest;
import com.springsource.util.osgi.manifest.BundleManifestFactory;
import java.lang.reflect.Field;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.osgi.framework.*;
import org.osgi.framework.wiring.FrameworkWiring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.webscripts.DeclarativeRegistry;
import org.springframework.extensions.webscripts.GUID;
import org.springframework.extensions.webscripts.Registry;
import org.springframework.extensions.webscripts.servlet.FormData.FormField;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import java.io.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import org.springframework.extensions.webscripts.Container;

/**
 * Helper for working with {@link Bundle}s.
 */
@Component
public class BundleHelper implements EventListener<SpringContextException>, FrameworkListener {
    private static Logger logger = LoggerFactory.getLogger(BundleHelper.class);

    @Autowired
    private BundleContext bundleContext = null;
    @Autowired
    private RepositoryStoreService repositoryStoreService = null;
    @Autowired
    private FileFolderService fileFolderService = null;
    @Autowired
    private ContentService contentService = null;
    @Autowired
    private NodeService nodeService = null;
    @Autowired
    @Resource(name = "webscripts.container")
    private Container webScriptsContainer = null;

    public BundleHelper() {}
    public BundleHelper(BundleContext bundleContext,
            RepositoryStoreService repositoryStoreService,
            FileFolderService fileFolderService,
            ContentService contentService,
            NodeService nodeService,
            Container webScriptsContainer) {
        this.bundleContext = bundleContext;
        this.repositoryStoreService = repositoryStoreService;
        this.fileFolderService = fileFolderService;
        this.contentService = contentService;
        this.nodeService = nodeService;
        this.webScriptsContainer = webScriptsContainer;
    }

    public BundleContext getBundleContext() {
        return this.bundleContext;
    }

    public String getBundleRepositoryLocation() {
        return repositoryStoreService.getBundleRepositoryLocation();
    }

    /**
     * async backlog of bundles to start when the package-admin is done refreshing dependencies
     */
    private ConcurrentLinkedQueue<Bundle> bundlesToStart = new ConcurrentLinkedQueue<Bundle>();

    /**
     * installBundle operations block on this queue until either an error or successful install is reported
     */
    private LinkedBlockingDeque<InstallResult> installResults = new LinkedBlockingDeque<InstallResult>();

    /* Main operations */

    @PostConstruct
    public ServiceRegistration registerEventListeners() {
        // get notified of Spring context start failures
        return bundleContext.registerService(EventListener.class, this, null);
    }

    /**
     * Obtains the {@link Bundle}s that comprise the core framework.
     */
    public List<Bundle> getFrameworkBundles() {
        return Arrays.stream(bundleContext.getBundles())
                .filter(bundle -> !BundleHelperCompanion.isDynamicExtension(bundle))
                .collect(Collectors.toList());
    }

    /**
     * Obtains the {@link Bundle}s that comprise the core framework.
     */
    public List<Bundle> getExtensionBundles() {
        return Arrays.stream(bundleContext.getBundles())
            .filter(bundle -> BundleHelperCompanion.isDynamicExtension(bundle))
            .collect(Collectors.toList());
    }

    /**
     * Obtains the {@link Bundle} for the given id.

     * @param id BundleId
     * *
     * @return The matching {@link Bundle} or null if no match could be found.
     */
    public Bundle getBundle(Long id) {
        return bundleContext.getBundle(id);
    }

    /**
     * Installs an uploaded file as a bundle in the repository.
     *
     *
     * This implementation first saves the upload to a temporary file. It then attempts to install the file as a bundle.
     * If this succeeds, it saves the bundle in the repository.

     * @param file form field
     * *
     * @return installed Bundle
     * *
     * @throws IOException
     * *
     * @throws BundleException
     */
    public Bundle installBundleInRepository(FormField file) throws IOException, BundleException {
        return installBundleInRepository(file.getContent());
    }

    /**
     * Installs a bundle using the given {@link Content} and filename.

     * @param content uploaded content
     * *
     * @return installted {@link Bundle}
     * *
     * @throws IOException
     * *
     * @throws BundleException
     */
    public Bundle installBundleInRepository(Content content) throws IOException, BundleException {
        try (InputStream stream = content.getInputStream()) {
            File tempFile = saveToTempFile(stream);
            return doInstallBundleInRepository(tempFile, null);
        }
    }

    public NodeRef uninstallAndDeleteBundle(Bundle bundle) throws BundleException {
        NodeRef matchingNode = null;
        if(bundle == null) {
            return null;
        }

        Matcher matcher = Pattern.compile("/Company Home(/.+)+/(.+\\.jar)$").matcher(bundle.getLocation());
        if (matcher.matches()) {
            String filename = matcher.group(2);
            NodeRef bundleFolder = repositoryStoreService.getBundleFolder(false);
            if (bundleFolder != null) {
                NodeRef file = fileFolderService.searchSimple(bundleFolder, filename);
                if (file != null) {
                    HashMap<QName, Serializable> properties = new HashMap<QName, Serializable>();
                    nodeService.addAspect(file, ContentModel.ASPECT_TEMPORARY, properties);
                    nodeService.deleteNode(file);
                    matchingNode = file;
                    bundle.uninstall();
                }
            }
        }

        return matchingNode;
    }

    public ServiceReference<?>[] getAllServices() throws InvalidSyntaxException {
        return bundleContext.getAllServiceReferences(null, null);
    }

    <T> T getService(Class<T> service) {
        ServiceReference<T> serviceReference = bundleContext.getServiceReference(service);
        if (serviceReference != null) {
            return bundleContext.getService(serviceReference);
        } else {
            return null;
        }
    }

    /* Utility operations */

    protected FrameworkWiring getFrameworkWiring() {
        return bundleContext.getBundle(0).adapt(FrameworkWiring.class);
    }

    Bundle doInstallBundleInRepository(File tempFile, String fileName) throws BundleException {
        File jarToInstall = tempFile;
        installResults.clear();
        try {
            BundleIdentifier identifier = getBundleIdentifier(jarToInstall);
            if (identifier == null) {
                jarToInstall = wrapPlainJar(tempFile, fileName);
                identifier = getBundleIdentifier(jarToInstall);
                if (identifier == null) {
                    throw new BundleException("Could not generate Bundle filename. Make sure the content is an OSGi bundle.");
                }
                String symbolicName = identifier.getSymbolicName();
                logger.info("Wrapped plain jar as a OSGi bundle: {}.", symbolicName);
            }
            fileName = identifier.toJarFilename();
            String location = generateRepositoryLocation(fileName);
            Bundle bundle = bundleContext.getBundle(location);

            // a classpath bundle cannot be replaced in a persistent way, so we only do temporary updates here
            boolean classpathBundle = false;
            if (bundle == null) {
                bundle = findBundleBySymbolicName(identifier);
                if (bundle != null) {
                    NodeRef deletedNode = uninstallAndDeleteBundle(bundle);
                    if (deletedNode != null) {
                        logger.warn("Deleted existing repository bundle {} with an identical Symbolic name: {}.", deletedNode, identifier.getSymbolicName());
                        bundle = null;
                    } else {
                        classpathBundle = true;
                    }
                }
            }

            InputStream inputStream = createStreamForFile(jarToInstall);
            if (bundle != null) {
                // we stop and delay restarting the bundle, as otherwise, the refresh would cause 2 immediate restarts,
                bundle.stop();
                bundle.update(inputStream);

                FrameworkWiring wiring = getFrameworkWiring();

                HashSet<Bundle> bundleSet = new HashSet<Bundle>();
                bundleSet.add(bundle);

                // resolve to synchronously assert dependencies are in order
                wiring.resolveBundles(bundleSet);

                if (!isFragmentBundle(bundle)) {
                    bundlesToStart.offer(bundle);

                    Bundle[] dependantBundles = wiring.getDependencyClosure(bundleSet)
                                                    .stream()
                                                    .filter(bndl -> bndl.getState() == Bundle.ACTIVE)
                                                    .toArray(Bundle[]::new);
                    ArrayList<Bundle> dependantBundlesList = new ArrayList<Bundle>(Arrays.asList(dependantBundles));
                    List<Bundle> dependantBundlesSorted = BundleDependencies.sortByDependencies(dependantBundlesList);

                    dependantBundlesSorted.sort(Collections.reverseOrder());

                    for (Bundle dependant : dependantBundlesSorted) {
                        dependant.stop();
                    }
                    for (Bundle dependant : dependantBundlesSorted) {
                        bundlesToStart.offer(dependant);
                    }

                    // async operation
                    wiring.refreshBundles(bundleSet, this);
                } else {
                    return bundle;
                }
            } else {
                bundle = bundleContext.installBundle(location, inputStream);
                if (!isFragmentBundle(bundle)) {
                    bundle.start();
                    installResults.add(new InstallResult(null));
                }
            }

            if (bundle != null) {
                if (!classpathBundle) {
                    BundleManifest manifest = createBundleManifest(bundle);
                    saveBundleInRepository(jarToInstall, fileName, manifest);
                } else {
                    logger.warn("Temporarily updated classpath bundle: {}, update will be reverted after restart.", bundle.getSymbolicName());
                }

                try {
                    BundleHelperCompanion.evaluateInstallationResult(installResults.poll(1, TimeUnit.MINUTES));
                } catch (InterruptedException tx) {
                    logger.warn("Timed out waiting for an installation result", tx);
                }

                resetWebScriptsCache();
            }

            return bundle;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            //jarToInstall.delete();
        }
    }

    protected BundleManifest createBundleManifest(Bundle bundle) {
        if(bundle == null) {
            return null;
        }
        return BundleManifestFactory.createBundleManifest(bundle.getHeaders());
    }

    protected InputStream createStreamForFile(File file) throws FileNotFoundException {
        return new FileInputStream(file);
    }

    private File wrapPlainJar(File tempFile, String fileName) {
        try {
            JarFile jar = new JarFile(tempFile);

            Analyzer analyzer = new Analyzer();
            String manifestVersion = ManifestUtils.getImplementationVersion(jar);
            if (manifestVersion != null) {
                analyzer.setBundleVersion(manifestVersion);
            }
            String name = ManifestUtils.getImplementationTitle(jar);
            if (name == null) {
                if (fileName == null) {
                    return tempFile;
                } else {
                    name = fileName.replaceFirst("^(.+)\\.\\w+$", "$1");
                }
            }
            analyzer.setBundleSymbolicName(name);

            analyzer.setJar(tempFile);
            analyzer.setImportPackage("*;resolution:=optional");
            analyzer.setExportPackage("*");
            analyzer.analyze();
            Manifest manifest = analyzer.calcManifest();
            analyzer.getJar().setManifest(manifest);
            File wrappedTempFile = File.createTempFile("wrapped", ".jar");
            analyzer.save(wrappedTempFile, true);
            return wrappedTempFile;
        } catch (Exception e) {
            logger.warn("Failed to wrap plain $tempFile jar using bnd.", e);
            return tempFile;
        }
    }

    protected Bundle findBundleBySymbolicName(BundleIdentifier identifier) throws BundleException {
        for(Bundle bundle : bundleContext.getBundles()) {
            if(bundle == null) {
                continue;
            }
            if(bundle.getSymbolicName().equals(identifier.getSymbolicName())) {
                return bundle;
            }
        }
        return null;
    }

    protected File saveToTempFile(InputStream data) throws IOException {
        File tempFile = File.createTempFile("dynamic-extensions-bundle", GUID.generate());
        tempFile.deleteOnExit();
        try (FileOutputStream outputStream = new FileOutputStream(tempFile, false)) {
            StreamUtils.copy(data, outputStream);
            return tempFile;
        }
    }

    protected BundleIdentifier getBundleIdentifier(File tempFile) throws IOException {
        BundleIdentifier identifier = null;
        try (JarFile jarFile = new JarFile(tempFile)) {
            Manifest manifest = jarFile.getManifest();
            Attributes attributes = manifest.getMainAttributes();
            String symbolicName = attributes.getValue(Constants.BUNDLE_SYMBOLICNAME);
            String version = attributes.getValue(Constants.BUNDLE_VERSION);
            if (StringUtils.hasText(symbolicName) && StringUtils.hasText(version)) {
                identifier = BundleIdentifier.fromSymbolicNameAndVersion(symbolicName, version);
            }
            return identifier;
        }
    }

    protected void saveBundleInRepository(File file, String filename, BundleManifest manifest)
            throws FileNotFoundException {
        NodeRef bundleFolder = repositoryStoreService.getBundleFolder(true);
        NodeRef nodeRef = fileFolderService.searchSimple(bundleFolder, filename);
        if (nodeRef == null) {
            nodeRef = fileFolderService.create(bundleFolder, filename, ContentModel.TYPE_CONTENT).getNodeRef();
        }
        String title = String.format("%s %s", manifest.getBundleName(), manifest.getBundleVersion());
        nodeService.setProperty(nodeRef, ContentModel.PROP_TITLE, title);

        // disable indexing
        nodeService.setProperty(nodeRef, ContentModel.PROP_DESCRIPTION, manifest.getBundleDescription());
        HashMap<QName, Serializable> props = new HashMap<>();
        props.put(ContentModel.PROP_IS_INDEXED, false);
        nodeService.addAspect(nodeRef, ContentModel.ASPECT_INDEX_CONTROL, props);

        ContentWriter writer = contentService.getWriter(nodeRef, ContentModel.PROP_CONTENT, true);
        writer.setMimetype(MimetypeMap.MIMETYPE_ZIP);
        writer.putContent(createStreamForFile(file));
    }

    protected String generateRepositoryLocation(String filename) {
        return String.format("%s/%s", getBundleRepositoryLocation(), filename);
    }

    protected Boolean isFragmentBundle(Bundle bundle) {
        return bundle != null && bundle.getHeaders().get(Constants.FRAGMENT_HOST) != null;
    }

    /**
     * The {@link DeclarativeRegistry} caches 404 results, which can hide new webscript deployments.
     * Unfortunately there is no public API for resetting this cache.
     */
    protected void resetWebScriptsCache() {
        Registry registry = webScriptsContainer.getRegistry();
        if (registry instanceof DeclarativeRegistry) {
            try {
                Field cacheField = DeclarativeRegistry.class.getDeclaredField("uriIndexCache");
                if (!cacheField.isAccessible()) {
                    cacheField.setAccessible(true);
                }
                AbstractMap<?,?> cache = (AbstractMap<?,?>)cacheField.get(registry);
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
    public Class<?>[] supportedEventTypes() {
        return new Class<?>[] { SpringContextException.class };
    }

    @Override
    public void frameworkEvent(FrameworkEvent event) {
        if (event.getType() == FrameworkEvent.PACKAGES_REFRESHED) {
            // start any bundles that were recently updated after the PackageAdmin has refreshed (restarted) any dependencies
            Bundle bundle = bundlesToStart.poll();
            while (bundle != null) {
                try {
                    bundle.start();
                    installResults.add(new InstallResult(null));
                } catch (BundleException bx) {
                    installResults.add(new InstallResult(bx));
                }
                bundle = bundlesToStart.poll();
            }
        }
    }

    class InstallResult{
        public Exception exception;

        InstallResult(Exception exception){
            this.exception = exception;
        }
    }

    public static class BundleHelperCompanion {
        private static Logger logger = LoggerFactory.getLogger(BundleHelperCompanion.class);

        static String ALFRESCO_DYNAMIC_EXTENSION_HEADER = "Alfresco-Dynamic-Extension";

        /**
         * Tests if the given bundle contains a Dynamic Extension.
         *
         *
         * This implementation looks if the bundle header `Alfresco-Dynamic-Extension` equals the String "true".
         */
        public static Boolean isDynamicExtension(Bundle bundle) {
            return bundle != null
                && "true".equals(bundle.getHeaders().get(ALFRESCO_DYNAMIC_EXTENSION_HEADER));
        }

        static void evaluateInstallationResult(InstallResult installResult) throws BundleException {
            if (installResult != null) {
                if (installResult.exception instanceof RuntimeException) {
                    throw (RuntimeException)installResult.exception;
                } else if (installResult.exception instanceof BundleException) {
                    throw (BundleException)installResult.exception;
                }
            }
        }
    }
}
