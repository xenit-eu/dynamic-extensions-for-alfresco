package com.github.dynamicextensionsalfresco.controlpanel

import org.mockito.Mockito.*
import org.mockito.Mockito.`when` as whenever
import org.osgi.framework.BundleContext
import org.mockito.Mockito
import org.mockito.ArgumentCaptor
import org.osgi.framework.FrameworkListener
import com.github.dynamicextensionsalfresco.event.EventListener
import org.mockito.Matchers.*
import com.github.dynamicextensionsalfresco.event.events.SpringContextException
import java.io.File
import org.junit.Test
import org.osgi.framework.Bundle
import java.io.FileInputStream
import java.io.StringReader
import java.io.InputStream
import java.io.ByteArrayInputStream
import org.osgi.framework.BundleException
import kotlin.test.assertEquals
import kotlin.test.fail
import kotlin.test.failsWith
import org.junit.Before
import com.github.dynamicextensionsalfresco.event.Event
import com.github.dynamicextensionsalfresco.event.impl.DefaultEventBus
import org.osgi.framework.ServiceReference
import org.springframework.beans.BeansException
import org.springframework.beans.BeanInstantiationException
import org.springframework.context.ApplicationContextException
import com.springsource.util.osgi.manifest.BundleManifest
import org.alfresco.service.cmr.repository.NodeRef
import org.osgi.service.packageadmin.PackageAdmin
import org.osgi.framework.FrameworkEvent

/**
 * @author Laurent Van der Linden
 */
public class BundleHelperTest {
    fun stageActors(update: Boolean, mockBundleProvider: (BundleContext) -> Bundle = {mock(javaClass<Bundle>())}): Actors {
        val bundleHelper = MockBundleHelper(update, mockBundleProvider)

        bundleHelper.bundleContext = mock(javaClass<BundleContext>())

        bundleHelper.registerEventListeners()

        whenever(bundleHelper.bundleContext.getBundles()).thenReturn(array<Bundle>())

        whenever(bundleHelper.bundleContext.getAllServiceReferences(eq(javaClass<EventListener<*>>().getName()), anyString())).thenReturn(array(mock(javaClass<ServiceReference<*>>())))
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
            actors.bundleHelper.doInstallBundleInRepository(null, "any")
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
            actors.bundleHelper.doInstallBundleInRepository(null, "any")
        }
    }

    Test
    fun installableBundle() {
        val actors = stageActors(update = false)

        whenever(actors.bundleContext.installBundle(anyString(), anyObject<InputStream>())).then {
            actors.frameworkListener!!.frameworkEvent(FrameworkEvent(FrameworkEvent.PACKAGES_REFRESHED, mock(javaClass<Bundle>())))

            mock(javaClass<Bundle>())
        }

        actors.bundleHelper.doInstallBundleInRepository(null, "any")
    }

    Test
    fun updateBundleWithInvalidManifest() {
        val actors = stageActors(update = true, mockBundleProvider = { bc ->
            val mockBundle = mock(javaClass<Bundle>())
            whenever(mockBundle.start()).thenThrow(BundleException("failed to resolve test bundle", BundleException.RESOLVE_ERROR))
            mockBundle
        })

        failsWith(javaClass<BundleException>()) {
            actors.bundleHelper.doInstallBundleInRepository(null, "any")
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
            actors.bundleHelper.doInstallBundleInRepository(null, "any")
        }
    }

    Test
    fun updateInstallableBundle() {
        val actors = stageActors(update = true)

        whenever(actors.bundleContext.installBundle(anyString(), anyObject<InputStream>())).then {
            actors.frameworkListener!!.frameworkEvent(FrameworkEvent(FrameworkEvent.PACKAGES_REFRESHED, mock(javaClass<Bundle>())))

            mock(javaClass<Bundle>())
        }

        actors.bundleHelper.doInstallBundleInRepository(null, "any")
    }
}

class MockBundleHelper(val update: Boolean, val mockBundle: (BundleContext) -> Bundle) : BundleHelper() {
    override fun getBundleIdentifier(tempFile: File?): BundleIdentifier? {
        return BundleIdentifier.fromSymbolicNameAndVersion("test-bundle", "1.0")
    }

    override fun getBundleRepositoryLocation(): String? {
        return "/app:any"
    }

    override fun createStreamForFile(file: File?): InputStream? {
        return ByteArrayInputStream(ByteArray(0))
    }

    override fun isFragmentBundle(bundle: Bundle?): Boolean {
        return false
    }

    override fun createBundleManifest(bundle: Bundle?): BundleManifest? {
        return null
    }

    override fun saveBundleInRepository(file: File?, filename: String?, manifest: BundleManifest?) {}

    override fun resetWebScriptsCache() {}

    override fun findBundleBySymbolicName(identifier: BundleIdentifier?): Bundle? {
        return if (update) mockBundle(bundleContext) else null
    }

    override fun uninstallAndDeleteBundle(bundle: Bundle?): NodeRef? {
        return null
    }

    override fun getPackageAdmin(): PackageAdmin? {
        val packageAdmin = mock(javaClass<PackageAdmin>())
        whenever(packageAdmin.refreshPackages(anyObject())).then {
            frameworkEvent(FrameworkEvent(FrameworkEvent.PACKAGES_REFRESHED, mock(javaClass<Bundle>())))
        }
        return packageAdmin
    }
}