package com.github.dynamicextensionsalfresco.controlpanel

import org.osgi.framework.Bundle

val Bundle.stateDescription: String
    get() = when (this.getState()) {
    Bundle.UNINSTALLED -> "uninstalled"
    Bundle.INSTALLED -> "installed"
    Bundle.RESOLVED -> "resolved"
    Bundle.STARTING -> "starting"
    Bundle.STOPPING -> "stopping"
    Bundle.ACTIVE -> "active"
    else -> "unknown"
}

val Bundle.isActive: Boolean
    get() = getState() == Bundle.ACTIVE