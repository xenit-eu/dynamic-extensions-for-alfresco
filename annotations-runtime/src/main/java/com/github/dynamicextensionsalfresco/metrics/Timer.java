package com.github.dynamicextensionsalfresco.metrics;

import java.util.function.Supplier;

/**
 * register timing information during a transaction and report after commit (reporting dependant of implementation)
 *
 * @author Laurent Van der Linden
 */
public interface Timer {

    void start(String label);

    void stop();

    boolean isEnabled();

    default <T, E extends Throwable> T time(Supplier<String> labelProvider, ThrowingSupplier<T, E> operation) throws E {
        if (isEnabled()) {
            start(labelProvider.get());
            try {
                return operation.get();
            } finally {
                stop();
            }
        } else {
            return operation.get();
        }
    }

    @FunctionalInterface
    interface ThrowingSupplier<T, E extends Throwable> {

        T get() throws E;
    }
}
