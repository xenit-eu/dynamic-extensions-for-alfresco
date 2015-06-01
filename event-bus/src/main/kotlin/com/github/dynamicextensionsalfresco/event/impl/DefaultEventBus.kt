package com.github.dynamicextensionsalfresco.event.impl

import com.github.dynamicextensionsalfresco.event.Event
import com.github.dynamicextensionsalfresco.event.EventBus
import com.github.dynamicextensionsalfresco.event.EventListener
import org.osgi.framework.BundleContext
import org.osgi.framework.InvalidSyntaxException
import org.osgi.framework.ServiceReference

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Synchronous implementation that only notifies listeners based on their generic signature.

 * @author Laurent Van der Linden
 */
public class DefaultEventBus(private val bundleContext: BundleContext) : EventBus {
    override fun <T : Event> publish(event: T) {
        bundleContext.getAllServiceReferences(javaClass<EventListener<Event>>().getName(), null)
            .map { bundleContext.getService(it) }
            .filterIsInstance(javaClass<EventListener<T>>())
            .filter { it.getSupportedEventTypes().any { it == event.javaClass } }
            .forEach { it.onEvent(event) }
    }
}
