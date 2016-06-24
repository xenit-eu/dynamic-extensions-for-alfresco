package com.github.dynamicextensionsalfresco.controlpanel.template

import com.github.dynamicextensionsalfresco.controlpanel.BundleHelper
import com.github.dynamicextensionsalfresco.osgi.stateDescription
import com.springsource.util.osgi.manifest.BundleManifestFactory
import com.springsource.util.osgi.manifest.ExportedPackage
import org.osgi.framework.Bundle
import org.osgi.framework.Constants
import org.osgi.framework.ServiceReference
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adapts an [Bundle] for display in a Freemarker template.

 * @author Laurens Fridael
 */
class TemplateBundle(private val bundle: Bundle, services: List<ServiceReference<Any?>>?) : Comparable<TemplateBundle> {
    private val FRAMEWORK_BUNDLE_ID = 0

    constructor(bundle: Bundle) : this(bundle, null)

    val services: List<TemplateServiceReference>

    init {
        this.services = services
                    ?.map { TemplateServiceReference(it) }
                    ?.sorted()
                    ?: emptyList()
    }

    val bundleId: Long
        get() = bundle.bundleId

    val symbolicName: String
        get() = bundle.symbolicName ?: "non OSGi jar file"

    val name: String
        get() = bundle.headers.get(Constants.BUNDLE_NAME)?.toString() ?: symbolicName

    val description: String?
        get() = bundle.headers.get(Constants.BUNDLE_DESCRIPTION)?.toString()

    val isDynamicExtension: Boolean
        get() = BundleHelper.isDynamicExtension(bundle)

    val isFragmentBundle: Boolean
        get() = bundle.headers.get(Constants.FRAGMENT_HOST) != null

    val location: String
        get() = bundle.location

    val lastModified: String?
        get() {
            val lastModified = bundle.lastModified
            if (lastModified > 0) {
                return SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z").format(Date(lastModified))
            } else {
                return null
            }
        }

    val version: String
        get() = bundle.version.toString()

    val store: String
        get() {
            if (bundle.location.startsWith("file:")) {
                return "filesystem"
            } else if (bundle.location.startsWith("/")) {
                return "repository"
            } else {
                return "n/a"
            }
        }

    val status: String?
        get() {
            return bundle.stateDescription
        }

    val exportPackage: String?
        get() = bundle.headers.get(Constants.EXPORT_PACKAGE)

    val documentationUrl: String?
        get() = bundle.headers.get(Constants.BUNDLE_DOCURL)

    val isDeleteable: Boolean
        get() = location.startsWith("/Company Home")

    val importedPackages: List<TemplateImportedPackage>
        get() {
            val packages = ArrayList<TemplateImportedPackage>()
            for (importedPackage in manifest.importPackage.importedPackages) {
                val bundlePackage = TemplateImportedPackage()
                bundlePackage.name = importedPackage.packageName
                val ceiling = importedPackage.version.ceiling
                if (ceiling != null) {
                    bundlePackage.maxVersion = ceiling.toString()
                }
                val floor = importedPackage.version.floor
                if (floor != null) {
                    bundlePackage.minVersion = floor.toString()
                }
                packages.add(bundlePackage)
            }
            return packages
        }

    val exportedPackages: List<ExportedPackage>
        get() = manifest.exportPackage.exportedPackages

    val manifest by lazy {
        BundleManifestFactory.createBundleManifest(bundle.headers)
    }


    override fun compareTo(other: TemplateBundle): Int {
        if (this.bundleId == FRAMEWORK_BUNDLE_ID.toLong()) {
            return Integer.MIN_VALUE
        } else if (other.bundleId == 0L) {
            return Integer.MAX_VALUE
        }

        val compare = this.name.compareTo(other.name, ignoreCase = true)
        if (compare == 0) {
            return this.version.compareTo(other.version)

        }

        return compare
    }
}
