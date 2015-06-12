package com.github.dynamicextensionsalfresco.event

/**
 * @author Laurent Van der Linden
 */
public interface EventBus {
    public fun <T : Event> publish(event: T)
}
