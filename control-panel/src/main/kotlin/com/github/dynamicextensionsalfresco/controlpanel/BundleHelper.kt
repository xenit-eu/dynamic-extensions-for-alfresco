package com.github.dynamicextensionsalfresco.controlpanel

import aQute.bnd.osgi.Analyzer
import com.github.dynamicextensionsalfresco.event.EventListener
import com.github.dynamicextensionsalfresco.event.events.SpringContextException
import com.github.dynamicextensionsalfresco.info
import com.github.dynamicextensionsalfresco.osgi.BundleDependencies
import com.github.dynamicextensionsalfresco.osgi.ManifestUtils
import com.github.dynamicextensionsalfresco.osgi.RepositoryStoreService
import com.github.dynamicextensionsalfresco.osgi.isActive
import com.springsource.util.osgi.manifest.BundleManifest
import com.springsource.util.osgi.manifest.BundleManifestFactory
import org.alfresco.model.ContentModel
import org.alfresco.repo.content.MimetypeMap
import org.alfresco.service.cmr.model.FileFolderService
import org.alfresco.service.cmr.repository.ContentService
import org.alfresco.service.cmr.repository.NodeRef
import org.alfresco.service.cmr.repository.NodeService
import org.alfresco.service.namespace.QName
import org.osgi.framework.*
import org.osgi.framework.wiring.FrameworkWiring
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.extensions.surf.util.Content
import org.springframework.extensions.webscripts.DeclarativeRegistry
import org.springframework.extensions.webscripts.servlet.FormData.FormField
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import java.io.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.TimeUnit
import java.util.jar.JarFile
import java.util.regex.Pattern
import javax.annotation.PostConstruct
import javax.annotation.Resource
import org.springframework.extensions.webscripts.Container as WSContainer

/**
 * Helper for working with [Bundle]s.

 * @author Laurens Fridael
 */
@Component
public open class BundleHelper constructor() : EventListener<SpringContextException>, FrameworkListener {

    @Autowired
    var bundleContext: BundleContext?=null;
    @Autowired
    var repositoryStoreService: RepositoryStoreService?=null;
    @Autowired
    var fileFolderService: FileFolderService?=null;
    @Autowired
    var contentService: ContentService?=null;
    @Autowired
    var nodeService: NodeService?=null;
    @Resource(name = "webscripts.container")
    var webScriptsContainer: WSContainer?=null



    open val bundleRepositoryLocation: String
        get() = repositoryStoreService!!.bundleRepositoryLocation

    /**
     * async backlog of bundles to start when the package-admin is done refreshing dependencies
     */
    private val bundlesToStart = ConcurrentLinkedQueue<Bundle>()

    /**
     * installBundle operations block on this queue until either an error or successful install is reported
     */
    private val installResults = LinkedBlockingDeque<InstallResult>()

    /* Main operations */

    @PostConstruct
    public fun registerEventListeners() {
        // get notified of Spring context start failures
        bundleContext!!.registerService(EventListener::class.java, this, null)
    }

    /**
     * Obtains the [Bundle]s that comprise the core framework.
     */
    val frameworkBundles: List<Bundle>
        get() = bundleContext!!.bundles
            .filter { !isDynamicExtension(it) }

    /**
     * Obtains the [Bundle]s that comprise the core framework.
     */
    val extensionBundles: List<Bundle>
        get() = bundleContext!!.bundles
            .filter { isDynamicExtension(it) }

    /**
     * Obtains the [Bundle] for the given id.

     * @param id BundleId
     * *
     * @return The matching [Bundle] or null if no match could be found.
     */
    public fun getBundle(id: Long): Bundle {
        return bundleContext!!.getBundle(id)
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
    public fun installBundleInRepository(file: FormField): Bundle? {
        val tempFile = saveToTempFile(file.inputStream)
        return doInstallBundleInRepository(tempFile, file.filename)
    }

    /**
     * Installs a bundle using the given [Content] and filename.

     * @param content uploaded content
     * *
     * @return installted Bundle
     * *
     * @throws IOException
     * *
     * @throws BundleException
     */
    public fun installBundleInRepository(content: Content): Bundle? {
        val tempFile = saveToTempFile(content.inputStream)
        return doInstallBundleInRepository(tempFile, null)
    }

    @Throws(BundleException::class)
    public open fun uninstallAndDeleteBundle(bundle: Bundle): NodeRef? {
        var matchingNode: NodeRef? = null
        val matcher = Pattern.compile("/Company Home(/.+)+/(.+\\.jar)$").matcher(bundle.location)
        if (matcher.matches()) {
            val filename = matcher.group(2)
            val bundleFolder = repositoryStoreService!!.getBundleFolder(false)
            if (bundleFolder != null) {
                val file = fileFolderService!!.searchSimple(bundleFolder, filename)
                if (file != null) {
                    val properties = emptyMap<QName, Serializable>()
                    nodeService!!.addAspect(file, ContentModel.ASPECT_TEMPORARY, properties)
                    nodeService!!.deleteNode(file)
                    matchingNode = file
                    bundle.uninstall()
                }
            }
        }

        return matchingNode
    }

    public fun getAllServices(): Array<out ServiceReference<*>>? {
        return bundleContext!!.getAllServiceReferences(null, null)
    }

    fun <T> getService(service: Class<T>): T? {
        val serviceReference = bundleContext!!.getServiceReference(service)
        if (serviceReference != null) {
            return bundleContext!!.getService(serviceReference)
        } else {
            return null
        }
    }

    /* Utility operations */

    protected open val frameworkWiring: FrameworkWiring
        get() = bundleContext!!.getBundle(0).adapt(FrameworkWiring::class.java)

    fun doInstallBundleInRepository(tempFile: File, fileName: String?): Bundle? {
        var jarToInstall = tempFile

        installResults.clear()

        try {
            var identifier: BundleIdentifier? = getBundleIdentifier(jarToInstall)
            if (identifier == null) {
                jarToInstall = wrapPlainJar(tempFile, fileName)
                identifier = getBundleIdentifier(jarToInstall)
                if (identifier == null) {
                    throw BundleException("Could not generate Bundle filename. Make sure the content is an OSGi bundle.")
                }
                val symbolicName = identifier.symbolicName
                logger.info { "Wrapped plain jar as a OSGi bundle: $symbolicName." }
            }
            val filename = identifier.toJarFilename()
            val location = generateRepositoryLocation(filename)
            var bundle: Bundle? = bundleContext!!.getBundle(location)

            // a classpath bundle cannot be replaced in a persistent way, so we only do temporary updates here
            var classpathBundle = false
            if (bundle == null) {
                bundle = findBundleBySymbolicName(identifier)
                if (bundle != null) {
                    val deletedNode = uninstallAndDeleteBundle(bundle)
                    if (deletedNode != null) {
                        logger.warn("Deleted existing repository bundle {} with an identical Symbolic name: {}.", deletedNode, identifier.symbolicName)
                        bundle = null
                    } else {
                        classpathBundle = true
                    }
                }
            }

            val inputStream = createStreamForFile(jarToInstall)
            if (bundle != null) {
                // we stop and delay restarting the bundle, as otherwise, the refresh would cause 2 immediate restarts,
                bundle.stop()
                bundle.update(inputStream)

                val wiring = frameworkWiring

                val bundleSet = setOf(bundle)

                // resolve to synchronously assert dependencies are in order
                wiring.resolveBundles(bundleSet)

                if (isFragmentBundle(bundle) == false) {
                    bundlesToStart.offer(bundle)
                    val dependantBundles = wiring.getDependencyClosure(bundleSet).filter { it.isActive }
                    val dependantBundlesSorted = BundleDependencies.sortByDependencies(dependantBundles)

                    for (dependendant in dependantBundlesSorted.reversed()) {
                        dependendant.stop()
                    }
                    for (dependendant in dependantBundlesSorted) {
                        bundlesToStart.offer(dependendant)
                    }

                    // async operation
                    wiring.refreshBundles(bundleSet, this)
                } else {
                    return bundle
                }
            } else {
                bundle = bundleContext!!.installBundle(location, inputStream)
                if (isFragmentBundle(bundle) == false) {
                    bundle!!.start()
                    installResults.add(InstallResult(null))
                }
            }

            if (bundle != null) {
                if (!classpathBundle) {
                    val manifest = createBundleManifest(bundle)
                    saveBundleInRepository(jarToInstall, filename, manifest)
                } else {
                    logger.warn("Temporarily updated classpath bundle: {}, update will be reverted after restart.", bundle.symbolicName)
                }

                try {
                    evaluateInstallationResult(installResults.poll(1, TimeUnit.MINUTES))
                } catch (tx: InterruptedException) {
                    logger.warn("Timed out waiting for an installation result", tx)
                }

                resetWebScriptsCache()
            }

            return bundle
        } finally {
            jarToInstall.delete()
        }
    }

    protected open fun createBundleManifest(bundle: Bundle): BundleManifest {
        return BundleManifestFactory.createBundleManifest(bundle.headers)
    }

    protected open fun createStreamForFile(file: File): InputStream {
        return FileInputStream(file)
    }

    private fun wrapPlainJar(tempFile: File, fileName: String?): File {
        try {
            val jar = JarFile(tempFile)

            val analyzer = Analyzer()
            val manifestVersion = ManifestUtils.getImplementationVersion(jar)
            if (manifestVersion != null) {
                analyzer.bundleVersion = manifestVersion
            }
            var name = ManifestUtils.getImplementationTitle(jar)
            if (name == null) {
                if (fileName == null) {
                    return tempFile
                } else {
                    name = fileName.replaceFirst("^(.+)\\.\\w+$".toRegex(), "$1")
                }
            }
            analyzer.setBundleSymbolicName(name)

            analyzer.setJar(tempFile)
            analyzer.setImportPackage("*;resolution:=optional")
            analyzer.setExportPackage("*")
            analyzer.analyze()
            val manifest = analyzer.calcManifest()
            analyzer.jar.manifest = manifest
            val wrappedTempFile = File.createTempFile("wrapped", ".jar")
            analyzer.save(wrappedTempFile, true)
            return wrappedTempFile
        } catch (e: Exception) {
            logger.warn("Failed to wrap plain $tempFile jar using bnd.", e)
            return tempFile
        }
    }

    protected open fun findBundleBySymbolicName(identifier: BundleIdentifier): Bundle? {
        return bundleContext!!.bundles
                .firstOrNull<Bundle?>{ it?.symbolicName == identifier.symbolicName }
    }

    protected fun saveToTempFile(data: InputStream): File {
        val tempFile = File.createTempFile("dynamic-extensions-bundle", null)
        tempFile.deleteOnExit()
        data.copyTo(FileOutputStream(tempFile))
        return tempFile
    }

    protected open fun getBundleIdentifier(tempFile: File): BundleIdentifier? {
        var identifier: BundleIdentifier? = null
        val jarFile = JarFile(tempFile)
        try {
            val manifest = jarFile.manifest
            val attributes = manifest.mainAttributes
            val symbolicName = attributes.getValue(Constants.BUNDLE_SYMBOLICNAME)
            val version = attributes.getValue(Constants.BUNDLE_VERSION)
            if (StringUtils.hasText(symbolicName) && StringUtils.hasText(version)) {
                identifier = BundleIdentifier.fromSymbolicNameAndVersion(symbolicName, version)
            }
            return identifier
        } finally {
            jarFile.close()
        }
    }

    protected open fun saveBundleInRepository(file: File, filename: String, manifest: BundleManifest) {
        val bundleFolder = repositoryStoreService!!.getBundleFolder(true)
        var nodeRef: NodeRef? = fileFolderService!!.searchSimple(bundleFolder, filename)
        if (nodeRef == null) {
            nodeRef = fileFolderService!!.create(bundleFolder, filename, ContentModel.TYPE_CONTENT).nodeRef
        }
        val title = "%s %s".format(manifest.bundleName, manifest.bundleVersion)
        nodeService!!.setProperty(nodeRef, ContentModel.PROP_TITLE, title)

        // disable indexing
        nodeService!!.setProperty(nodeRef, ContentModel.PROP_DESCRIPTION, manifest.bundleDescription)
        nodeService!!.addAspect(nodeRef, ContentModel.ASPECT_INDEX_CONTROL, mapOf(
                ContentModel.PROP_IS_INDEXED to false
        ));

        val writer = contentService!!.getWriter(nodeRef, ContentModel.PROP_CONTENT, true)
        writer.mimetype = MimetypeMap.MIMETYPE_ZIP
        writer.putContent(createStreamForFile(file))
    }

    protected fun generateRepositoryLocation(filename: String): String {
        return "%s/%s".format(bundleRepositoryLocation, filename)
    }

    protected open fun isFragmentBundle(bundle: Bundle): Boolean {
        return bundle.headers.get(Constants.FRAGMENT_HOST) != null
    }

    /**
     * The DeclarativeRegistry caches 404 results, which can hide new webscript deployments.
     * Unfortunately there is no public API for resetting this cache.
     */
    @Suppress("UNCHECKED_CAST")
    protected open fun resetWebScriptsCache() {
        val registry = webScriptsContainer!!.registry
        if (registry is DeclarativeRegistry) {
            try {
                val cacheField = DeclarativeRegistry::class.java.getDeclaredField("uriIndexCache")
                if (!cacheField.isAccessible) {
                    cacheField.isAccessible = true
                }
                val cache = cacheField.get(registry) as MutableMap<Any, Any>
                cache.clear()
            } catch (e: Exception) {
                logger.error("failed to reset webscript cache", e)
            }
        }
    }

    override fun onEvent(event: SpringContextException) {
        installResults.add(InstallResult(event.exception))
    }

    override val supportedEventTypes: Array<Class<*>> = arrayOf(SpringContextException::class.java)

    override fun frameworkEvent(event: FrameworkEvent) {
        if (event.type == FrameworkEvent.PACKAGES_REFRESHED) {
            // start any bundles that were recently updated after the PackageAdmin has refreshed (restarted) any dependencies
            var bundle = bundlesToStart.poll()
            while (bundle != null) {
                try {
                    bundle.start()
                    installResults.add(InstallResult(null))
                } catch (bx: BundleException) {
                    installResults.add(InstallResult(bx))
                }
                bundle = bundlesToStart.poll()
            }
        }
    }

    private class InstallResult(public val exception: Exception?)

    companion object {
        private val logger = LoggerFactory.getLogger(BundleHelper::class.java)

        private val ALFRESCO_DYNAMIC_EXTENSION_HEADER = "Alfresco-Dynamic-Extension"

        /**
         * Tests if the given bundle contains a Dynamic Extension.
         *
         *
         * This implementation looks if the bundle header `Alfresco-Dynamic-Extension` equals the String "true".
         */
        public fun isDynamicExtension(bundle: Bundle): Boolean {
            return "true" == bundle.headers.get(ALFRESCO_DYNAMIC_EXTENSION_HEADER)
        }

        private fun evaluateInstallationResult(installResult: InstallResult?) {
            if (installResult != null) {
                if (installResult.exception is RuntimeException) {
                    throw installResult.exception
                } else if (installResult.exception is BundleException) {
                    throw installResult.exception
                }
            }
        }
    }
}
