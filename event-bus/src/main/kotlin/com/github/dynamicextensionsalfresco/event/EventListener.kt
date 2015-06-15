package com.github.dynamicextensionsalfresco.event

/**
 * @author Laurent Van der Linden
 */
public interface EventListener<T : Event> {
    public fun onEvent(event: T)
    public val supportedEventTypes: Array<Class<*>>
}
