package com.github.dynamicextensionsalfresco.event;

/**
 * @author Laurent Van der Linden
 */
public interface EventListener<T extends Event> {

    void onEvent(T event);

    Class<?>[] supportedEventTypes();

}
