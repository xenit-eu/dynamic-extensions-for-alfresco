package com.github.dynamicextensionsalfresco.controlpanel

import com.github.dynamicextensionsalfresco.event.EventListener
import com.github.dynamicextensionsalfresco.event.events.SpringContextException
import com.github.dynamicextensionsalfresco.event.impl.DefaultEventBus
import com.github.dynamicextensionsalfresco.osgi.RepositoryStoreService
import com.springsource.util.osgi.manifest.BundleManifest
import com.springsource.util.osgi.manifest.internal.StandardBundleManifest
import com.springsource.util.osgi.manifest.parse.DummyParserLogger
import org.alfresco.service.cmr.model.FileFolderService
import org.alfresco.service.cmr.repository.ContentService
import org.alfresco.service.cmr.repository.NodeRef
import org.alfresco.service.cmr.repository.NodeService
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.*
import org.mockito.Mockito.mock
import org.osgi.framework.*
import org.osgi.framework.wiring.FrameworkWiring
import org.springframework.beans.BeansException
import org.springframework.context.ApplicationContextException
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream
import kotlin.test.assertFailsWith
import org.mockito.Mockito.`when` as whenever

/**
 * @author Laurent Van der Linden
 */
class BundleHelperTest {
    fun stageActors(update: Boolean, mockBundleProvider: (BundleContext) -> Bundle = {mock(Bundle::class.java)}): Actors {
        val bundleHelper = MockBundleHelper(update, mockBundleProvider, mock(BundleContext::class.java),
                mock(RepositoryStoreService::class.java), mock(FileFolderService::class.java),
                mock(ContentService::class.java), mock(NodeService::class.java),
                mock(org.springframework.extensions.webscripts.Container::class.java))

        bundleHelper.registerEventListeners()

        whenever(bundleHelper.bundleContext!!.bundles).thenReturn(arrayOf())

        whenever(bundleHelper.bundleContext!!.getAllServiceReferences(eq(EventListener::class.java.name), anyString()))
                .thenReturn(arrayOf(mock(ServiceReference::class.java)))
        whenever(bundleHelper.bundleContext!!.getService(anyObject<ServiceReference<*>>())).thenReturn(bundleHelper)

        return Actors(bundleHelper, bundleHelper.bundleContext!!, bundleHelper)
    }

    class Actors(val bundleHelper: BundleHelper, val bundleContext: BundleContext, val frameworkListener: FrameworkListener?)

    @Test
    fun bundleWithInvalidManifest() {
        val actors = stageActors(update = false)

        whenever(actors.bundleContext.installBundle(anyString(), anyObject())).thenThrow(BundleException("cannot resolve some crazy import"))

        assertFailsWith(BundleException::class, {
            actors.bundleHelper.doInstallBundleInRepository(File("."), ".")!!
        })
    }

    @Test
    fun bundleWithInvalidSpringConfig() {
        val actors = stageActors(update = false)

        whenever(actors.bundleContext.installBundle(anyString(), anyObject<InputStream>())).then {
            DefaultEventBus(actors.bundleContext).publish(SpringContextException(mock(Bundle::class.java), ApplicationContextException("Spring could not autowire some stuff")))
            mock(Bundle::class.java)
        }

        assertFailsWith(BeansException::class, {
            actors.bundleHelper.doInstallBundleInRepository(File("."), "any")!!
        })
    }

    @Test
    fun installableBundle() {
        val actors = stageActors(update = false)

        whenever(actors.bundleContext.installBundle(anyString(), anyObject<InputStream>())).then {
            actors.frameworkListener!!.frameworkEvent(FrameworkEvent(FrameworkEvent.PACKAGES_REFRESHED, mock(Bundle::class.java), null))

            mock(Bundle::class.java)
        }

        actors.bundleHelper.doInstallBundleInRepository(File("."), "any")
    }

    @Test
    fun updateBundleWithInvalidManifest() {
        val actors = stageActors(update = true, mockBundleProvider = { bc ->
            val mockBundle = mock(Bundle::class.java)
            whenever(mockBundle.start()).thenThrow(BundleException("failed to resolve test bundle", BundleException.RESOLVE_ERROR))
            mockBundle
        })

        assertFailsWith(BundleException::class, {
            actors.bundleHelper.doInstallBundleInRepository(File("."), "any")!!
        })
    }

    @Test
    fun updateBundleWithInvalidSpringConfig() {
        val actors = stageActors(update = true, mockBundleProvider = { bundleContext ->
            val mockBundle = mock(Bundle::class.java)
            whenever(mockBundle.start()).then {
                DefaultEventBus(bundleContext).publish(SpringContextException(mock(Bundle::class.java), ApplicationContextException("Spring could not autowire some stuff")))
            }
            mockBundle
        })

        assertFailsWith(BeansException::class, {
            actors.bundleHelper.doInstallBundleInRepository(File("."), "any")!!
        })
    }

    @Test
    fun updateInstallableBundle() {
        val actors = stageActors(update = true)

        whenever(actors.bundleContext.installBundle(anyString(), anyObject<InputStream>())).then {
            actors.frameworkListener!!.frameworkEvent(FrameworkEvent(FrameworkEvent.PACKAGES_REFRESHED, mock(Bundle::class.java), null))

            mock(Bundle::class.java)
        }

        actors.bundleHelper.doInstallBundleInRepository(File("."), "any")
    }
}

class MockBundleHelper(val update: Boolean, val mockBundle: (BundleContext) -> Bundle, bundleContext: BundleContext
                       , repositoryStoreService: RepositoryStoreService, fileFolderService: FileFolderService,
                       contentService: ContentService, nodeservice: NodeService,
                       webScriptsContainer: org.springframework.extensions.webscripts.Container)
        : BundleHelper(bundleContext, repositoryStoreService, fileFolderService, contentService, nodeservice, webScriptsContainer) {
    override fun getBundleIdentifier(tempFile: File): BundleIdentifier? {
        return BundleIdentifier.fromSymbolicNameAndVersion("test-bundle", "1.0")
    }

    override val bundleRepositoryLocation: String
        get() = "/app:any"

    override fun createStreamForFile(file: File): InputStream {
        return ByteArrayInputStream(ByteArray(0))
    }

    override fun isFragmentBundle(bundle: Bundle): Boolean {
        return false
    }

    override fun createBundleManifest(bundle: Bundle): BundleManifest {
        return StandardBundleManifest(DummyParserLogger())
    }

    override fun saveBundleInRepository(file: File, filename: String, manifest: BundleManifest) {}

    override fun resetWebScriptsCache() {}

    override fun findBundleBySymbolicName(identifier: BundleIdentifier): Bundle? {
        return if (update) mockBundle(bundleContext!!) else null
    }

    override fun uninstallAndDeleteBundle(bundle: Bundle): NodeRef? {
        return null
    }

    override val frameworkWiring: FrameworkWiring
        get() {
            val wiring = mock(FrameworkWiring::class.java)
            val frameworkListener = ArgumentCaptor.forClass(FrameworkListener::class.java)
            whenever(wiring.refreshBundles(anyObject(), frameworkListener.capture())).then {
                frameworkListener.value.frameworkEvent(
                        FrameworkEvent(FrameworkEvent.PACKAGES_REFRESHED, mock(Bundle::class.java), null)
                )
            }
            return wiring
        }
}