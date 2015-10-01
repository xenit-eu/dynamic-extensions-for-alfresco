package com.github.dynamicextensionsalfresco

import org.slf4j.Logger

/**
 * Logging helpers to allow concise debug statements without anonymous class overhead
 *
 * @author Laurent Van der Linden
 */
inline fun Logger.debug(p: () -> String) {
    if (isDebugEnabled) debug(p())
}

inline fun Logger.info(p: () -> String) {
    if (isInfoEnabled) info(p())
}

inline fun Logger.warn(p: () -> String) {
    if (isWarnEnabled) warn(p())
}

