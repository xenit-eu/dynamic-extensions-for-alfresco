package com.github.dynamicextensionsalfresco.metrics

/**
 * register timing information during a transaction and report after commit
 * (reporting dependant of implementation)
 *
 * @author Laurent Van der Linden
 */
public interface Timer {
    fun start(label: String)

    fun stop()

    val enabled: Boolean
}

inline fun Timer.time(labelProvider: () -> String, operation: () -> Unit) {
    if (enabled) {
        start(labelProvider.invoke())
        try {
            operation()
        } finally {
            stop()
        }
    } else {
        operation()
    }
}
