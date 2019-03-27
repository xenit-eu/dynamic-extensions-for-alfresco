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

    default <T> T time(Supplier<String> labelProvider, Supplier<T> operation) {
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
}
