package com.github.dynamicextensionsalfresco.event.impl;

import com.github.dynamicextensionsalfresco.event.Event;
import com.github.dynamicextensionsalfresco.event.EventBus;
import com.github.dynamicextensionsalfresco.event.EventListener;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import javax.validation.constraints.NotNull;
import java.util.Arrays;

/**
 * Synchronous implementation that only notifies listeners based on their supported events.
 *
 * @author Laurent Van der Linden
 */
public final class DefaultEventBus implements EventBus {

    private final BundleContext bundleContext;

    public DefaultEventBus(@NotNull BundleContext bundleContext) {
        if (bundleContext == null) {
            throw new IllegalArgumentException("bundleContext is null");
        }

        this.bundleContext = bundleContext;
    }

    @SuppressWarnings("unchecked")
    public void publish(@NotNull final Event event) {
        if (event == null) {
            throw new IllegalArgumentException("event is null");
        }

        try {
            ServiceReference<?>[] serviceReferences = bundleContext
                    .getAllServiceReferences(EventListener.class.getName(), null);
            if (serviceReferences == null || serviceReferences.length == 0) {
                return;
            }
            Arrays.stream(serviceReferences)
                    .map(bundleContext::getService)
                    .filter(e -> e instanceof EventListener)
                    .filter(e -> Arrays.stream(((EventListener) e).supportedEventTypes())
                            .anyMatch(it -> it == event.getClass()))
                    .forEach(e -> ((EventListener<Event>) e).onEvent(event));
        } catch (InvalidSyntaxException e) {
            throw new IllegalStateException("Strange, 'InvalidSyntaxException' should not be thrown if filter is null?",
                    e);
        }
    }
}
