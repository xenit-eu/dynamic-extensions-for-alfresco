package com.github.dynamicextensionsalfresco.event.impl

import com.github.dynamicextensionsalfresco.event.Event
import com.github.dynamicextensionsalfresco.event.EventBus
import com.github.dynamicextensionsalfresco.event.EventListener
import org.osgi.framework.BundleContext

/**
 * Synchronous implementation that only notifies listeners based on their generic signature.

 * @author Laurent Van der Linden
 */
public class DefaultEventBus(private val bundleContext: BundleContext) : EventBus {
    override fun <T : Event> publish(event: T) {
        bundleContext.getAllServiceReferences(javaClass<EventListener<Event>>().getName(), null)
            .map { bundleContext.getService(it) }
            .filterIsInstance(javaClass<EventListener<T>>())
            .filter { it.supportedEventTypes.any { it == event.javaClass } }
            .forEach { it.onEvent(event) }
    }
}
