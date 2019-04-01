package com.github.dynamicextensionsalfresco.event.events;

import com.github.dynamicextensionsalfresco.event.Event;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.osgi.framework.Bundle;

/**
 * @author Laurent Van der Linden
 */
public final class SpringContextException implements Event {

    @NotNull
    private final Bundle bundle;
    @NotNull
    private final Exception exception;

    public SpringContextException(@NotNull Bundle bundle, @NotNull Exception exception) {
        Intrinsics.checkParameterIsNotNull(bundle, "bundle");
        Intrinsics.checkParameterIsNotNull(exception, "exception");

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
