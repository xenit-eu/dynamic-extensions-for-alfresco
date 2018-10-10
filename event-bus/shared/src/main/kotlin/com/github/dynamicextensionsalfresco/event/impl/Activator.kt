package com.github.dynamicextensionsalfresco.event.impl

import com.github.dynamicextensionsalfresco.event.EventBus
import org.osgi.framework.BundleActivator
import org.osgi.framework.BundleContext

/**
 * @author Laurent Van der Linden
 */
public class Activator : BundleActivator {
    @Throws(Exception::class)
    override fun start(context: BundleContext) {
        context.registerService(EventBus::class.java, DefaultEventBus(context), null)
    }

    @Throws(Exception::class)
    override fun stop(context: BundleContext) {
    }
}
