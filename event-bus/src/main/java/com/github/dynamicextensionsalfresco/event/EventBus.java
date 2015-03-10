package com.github.dynamicextensionsalfresco.event;

/**
 * @author Laurent Van der Linden
 */
public interface EventBus {
	<T extends Event> void publish(T event);
}
