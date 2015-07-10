package com.github.dynamicextensionsalfresco.controlpanel

import com.github.dynamicextensionsalfresco.event.Event
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
import org.mockito.Matchers.anyObject
import org.mockito.Matchers.anyString
import org.mockito.Matchers.eq
import org.mockito.Mockito.mock
import org.osgi.framework.*
import org.osgi.framework.wiring.FrameworkWiring
import org.osgi.service.packageadmin.PackageAdmin
import org.springframework.beans.BeansException
import org.springframework.context.ApplicationContextException
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream
import kotlin.test.failsWith
import org.mockito.Mockito.`when` as whenever

/**
 * @author Laurent Van der Linden
 */
public class BundleHelperTest {
    fun stageActors(update: Boolean, mockBundleProvider: (BundleContext) -> Bundle = {mock(javaClass<Bundle>())}): Actors {
        val bundleHelper = MockBundleHelper(update, mockBundleProvider, mock(javaClass<BundleContext>()),
                mock(javaClass<RepositoryStoreService>()), mock(javaClass<FileFolderService>()),
                mock(javaClass<ContentService>()), mock(javaClass<NodeService>()),
                mock(javaClass<org.springframework.extensions.webscripts.Container>()))

        bundleHelper.registerEventListeners()

        whenever(bundleHelper.bundleContext.getBundles()).thenReturn(arrayOf())

        whenever(bundleHelper.bundleContext.getAllServiceReferences(eq(javaClass<EventListener<*>>().getName()), anyString()))
                .thenReturn(arrayOf(mock(javaClass<ServiceReference<*>>())))
        whenever(bundleHelper.bundleContext.getService(anyObject<ServiceReference<*>>())).thenReturn(bundleHelper)

        return Actors(bundleHelper, bundleHelper, bundleHelper.bundleContext, bundleHelper)
    }

    class Actors(val bundleHelper: BundleHelper, val eventListener: EventListener<out Event>,
                 val bundleContext: BundleContext, val frameworkListener: FrameworkListener?)

    Test
    fun bundleWithInvalidManifest() {
        val actors = stageActors(update = false)

        whenever(actors.bundleContext.installBundle(anyString(), anyObject())).thenThrow(BundleException("cannot resolve some crazy import"))

        failsWith(javaClass<BundleException>()) {
            actors.bundleHelper.doInstallBundleInRepository(File("."), ".")!!
        }
    }

    Test
    fun bundleWithInvalidSpringConfig() {
        val actors = stageActors(update = false)

        whenever(actors.bundleContext.installBundle(anyString(), anyObject<InputStream>())).then {
            DefaultEventBus(actors.bundleContext).publish(SpringContextException(mock(javaClass<Bundle>()), ApplicationContextException("Spring could not autowire some stuff")))
            mock(javaClass<Bundle>())
        }

        failsWith(javaClass<BeansException>()) {
            actors.bundleHelper.doInstallBundleInRepository(File("."), "any")!!
        }
    }

    Test
    fun installableBundle() {
        val actors = stageActors(update = false)

        whenever(actors.bundleContext.installBundle(anyString(), anyObject<InputStream>())).then {
            actors.frameworkListener!!.frameworkEvent(FrameworkEvent(FrameworkEvent.PACKAGES_REFRESHED, mock(javaClass<Bundle>())))

            mock(javaClass<Bundle>())
        }

        actors.bundleHelper.doInstallBundleInRepository(File("."), "any")
    }

    Test
    fun updateBundleWithInvalidManifest() {
        val actors = stageActors(update = true, mockBundleProvider = { bc ->
            val mockBundle = mock(javaClass<Bundle>())
            whenever(mockBundle.start()).thenThrow(BundleException("failed to resolve test bundle", BundleException.RESOLVE_ERROR))
            mockBundle
        })

        failsWith(javaClass<BundleException>()) {
            actors.bundleHelper.doInstallBundleInRepository(File("."), "any")!!
        }
    }

    Test
    fun updateBundleWithInvalidSpringConfig() {
        val actors = stageActors(update = true, mockBundleProvider = { bundleContext ->
            val mockBundle = mock(javaClass<Bundle>())
            whenever(mockBundle.start()).then {
                DefaultEventBus(bundleContext).publish(SpringContextException(mock(javaClass<Bundle>()), ApplicationContextException("Spring could not autowire some stuff")))
            }
            mockBundle
        })

        failsWith(javaClass<BeansException>()) {
            actors.bundleHelper.doInstallBundleInRepository(File("."), "any")!!
        }
    }

    Test
    fun updateInstallableBundle() {
        val actors = stageActors(update = true)

        whenever(actors.bundleContext.installBundle(anyString(), anyObject<InputStream>())).then {
            actors.frameworkListener!!.frameworkEvent(FrameworkEvent(FrameworkEvent.PACKAGES_REFRESHED, mock(javaClass<Bundle>())))

            mock(javaClass<Bundle>())
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
        return if (update) mockBundle(bundleContext) else null
    }

    override fun uninstallAndDeleteBundle(bundle: Bundle): NodeRef? {
        return null
    }

    override val frameworkWiring: FrameworkWiring
        get() {
            val wiring = mock(javaClass<FrameworkWiring>())
            val frameworkListener = ArgumentCaptor.forClass(javaClass<FrameworkListener>())
            whenever(wiring.refreshBundles(anyObject(), frameworkListener.capture())).then {
                frameworkListener.getValue().frameworkEvent(
                        FrameworkEvent(FrameworkEvent.PACKAGES_REFRESHED, mock(javaClass<Bundle>()))
                )
            }
            return wiring
        }
}