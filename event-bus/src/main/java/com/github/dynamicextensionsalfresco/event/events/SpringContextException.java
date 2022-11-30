package com.github.dynamicextensionsalfresco.event.events;

import com.github.dynamicextensionsalfresco.event.Event;
import org.osgi.framework.Bundle;

import javax.validation.constraints.NotNull;

/**
 * @author Laurent Van der Linden
 */
public final class SpringContextException implements Event {

    @NotNull
    private final Bundle bundle;
    @NotNull
    private final Exception exception;

    public SpringContextException(@NotNull Bundle bundle, @NotNull Exception exception) {
        if (bundle == null) {
            throw new IllegalArgumentException("bundle is null");
        }
        if (exception == null) {
            throw new IllegalArgumentException("exception is null");
        }

        this.bundle = bundle;
        this.exception = exception;
    }

    @NotNull
    public final Bundle getBundle() {
        return this.bundle;
    }

    @NotNull
    public final Exception getException() {
        return this.exception;
    }
}
