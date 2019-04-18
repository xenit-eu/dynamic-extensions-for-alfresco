package com.github.dynamicextensionsalfresco.event.impl;

import com.github.dynamicextensionsalfresco.event.EventBus;
import org.jetbrains.annotations.NotNull;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * @author Laurent Van der Linden
 */
public final class Activator implements BundleActivator {

    public void start(@NotNull BundleContext context) {
        if (context == null) {
            throw new IllegalArgumentException("context is null");
        }
        context.registerService(EventBus.class, new DefaultEventBus(context), null);
    }

    public void stop(@NotNull BundleContext context) {
        if (context == null) {
            throw new IllegalArgumentException("context is null");
        }
    }
}
