package com.github.dynamicextensionsalfresco.event.impl

import com.github.dynamicextensionsalfresco.event.EventBus
import org.osgi.framework.BundleActivator
import org.osgi.framework.BundleContext

/**
 * @author Laurent Van der Linden
 */
public class Activator : BundleActivator {
    throws(Exception::class)
    override fun start(context: BundleContext) {
        context.registerService(javaClass<EventBus>(), DefaultEventBus(context), null)
    }

    throws(Exception::class)
    override fun stop(context: BundleContext) {
    }
}
