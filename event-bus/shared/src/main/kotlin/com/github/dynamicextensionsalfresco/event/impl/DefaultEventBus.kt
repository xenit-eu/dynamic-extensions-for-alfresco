package com.github.dynamicextensionsalfresco.event.impl

import com.github.dynamicextensionsalfresco.event.Event
import com.github.dynamicextensionsalfresco.event.EventBus
import com.github.dynamicextensionsalfresco.event.EventListener
import org.osgi.framework.BundleContext

/**
 * Synchronous implementation that only notifies listeners based on their supported events.

 * @author Laurent Van der Linden
 */
public class DefaultEventBus(private val bundleContext: BundleContext) : EventBus {
    @Suppress("UNCHECKED_CAST")
    override fun publish(event: Event) {
        bundleContext.getAllServiceReferences(EventListener::class.java.name, null)
                .asSequence()
                .map { bundleContext.getService(it) }
                .filterIsInstance(EventListener::class.java)
                .filter { it.supportedEventTypes.any { it == event.javaClass } }
                .forEach { (it as EventListener<Event>).onEvent(event) }
    }
}
