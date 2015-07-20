package com.github.dynamicextensionsalfresco.osgi

import com.springsource.util.osgi.manifest.BundleManifest
import com.springsource.util.osgi.manifest.BundleManifestFactory
import org.osgi.framework.Bundle
import kotlin.properties.Delegates

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

val Bundle.manifest: BundleManifest
    get() = BundleManifestFactory.createBundleManifest(getHeaders())