package com.github.dynamicextensionsalfresco.osgi

import com.github.dynamicextensionsalfresco.osgi.manifest
import org.osgi.framework.Bundle

/**
 * Sort a list of bundles by their dependency graph.
 * Bundles without or satisfied dependencies come first.
 *
 * @author Laurent Van der Linden.
 */
public object BundleDependencies : DependencyMetadataProvider<BundleDependencies.BundleDescriptor> {
    fun sortByDependencies(bundles: List<Bundle>): List<Bundle> {
        if (bundles.size() == 1) {
            return bundles
        }

        val descriptors = bundles.map { BundleDescriptor(it) }

        return DependencySorter.sort(descriptors, this).map { it.bundle }
    }

    override fun imports(item: BundleDescriptor): Collection<*> {
        return item.manifest.getImportPackage().getImportedPackages().map { it.getPackageName() }
    }

    override fun exports(item: BundleDescriptor): Collection<*> {
        return item.manifest.getExportPackage().getExportedPackages().map { it.getPackageName() }
    }

    override val allowCircularReferences: Boolean
        // SLF4J has a circular reference
        get() = true

    /**
     * Cache the manifest
     */
    data class BundleDescriptor(val bundle: Bundle) {
        val manifest = bundle.manifest

        override fun toString(): String {
            return "%3d: %s".format(bundle.getBundleId(), bundle.getSymbolicName())
        }
    }
}