package com.github.dynamicextensionsalfresco.event.impl;

import com.github.dynamicextensionsalfresco.event.Event;
import com.github.dynamicextensionsalfresco.event.EventBus;
import com.github.dynamicextensionsalfresco.event.EventListener;
import java.util.Arrays;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;

/**
 * Synchronous implementation that only notifies listeners based on their supported events.
 *
 * @author Laurent Van der Linden
 */
public final class DefaultEventBus implements EventBus {

    private final BundleContext bundleContext;

    public DefaultEventBus(@NotNull BundleContext bundleContext) {
        Intrinsics.checkParameterIsNotNull(bundleContext, "bundleContext");

        this.bundleContext = bundleContext;
    }

    @SuppressWarnings("unchecked")
    public void publish(@NotNull final Event event) {
        Intrinsics.checkParameterIsNotNull(event, "event");

        try {
            Arrays.stream(bundleContext.getAllServiceReferences(EventListener.class.getName(), null))
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
