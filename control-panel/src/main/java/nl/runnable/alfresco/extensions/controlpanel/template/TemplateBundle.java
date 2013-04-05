package nl.runnable.alfresco.extensions.controlpanel.template;

import java.util.ArrayList;
import java.util.List;

import nl.runnable.alfresco.extensions.controlpanel.BundleHelper;

import org.eclipse.osgi.framework.internal.core.Constants;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;
import org.springframework.util.Assert;

import com.springsource.util.osgi.manifest.BundleManifest;
import com.springsource.util.osgi.manifest.BundleManifestFactory;
import com.springsource.util.osgi.manifest.ExportedPackage;
import com.springsource.util.osgi.manifest.ImportedPackage;

/**
 * Adapts an {@link Bundle} for display purposes in a Freemarker template.
 * 
 * @author Laurens Fridael
 * 
 */
public class TemplateBundle implements Comparable<TemplateBundle> {

	private static final int FRAMEWORK_BUNDLE_ID = 0;

	private final Bundle bundle;

	private BundleManifest manifest;

	public TemplateBundle(final Bundle bundle) {
		Assert.notNull(bundle);
		this.bundle = bundle;
	}

	public long getBundleId() {
		return bundle.getBundleId();
	}

	public String getSymbolicName() {
		return bundle.getSymbolicName();
	}

	public String getName() {
		return bundle.getHeaders().get(Constants.BUNDLE_NAME);
	}

	public String getDescription() {
		return bundle.getHeaders().get(Constants.BUNDLE_DESCRIPTION);
	}

	public boolean isDynamicExtension() {
		return BundleHelper.isDynamicExtension(bundle);
	}

	public String getLocation() {
		return bundle.getLocation();
	}

	public long getLastModified() {
		return bundle.getLastModified();
	}

	public String getVersion() {
		return bundle.getVersion().toString();
	}

	public String getStatus() {
		switch (bundle.getState()) {
		case Bundle.UNINSTALLED:
			return "uninstalled";
		case Bundle.INSTALLED:
			return "installed";
		case Bundle.RESOLVED:
			return "resolved";
		case Bundle.STARTING:
			return "starting";
		case Bundle.STOPPING:
			return "stopping";
		case Bundle.ACTIVE:
			return "active";
		default:
			return null;
		}
	}

	public String getExportPackage() {
		return bundle.getHeaders().get(Constants.EXPORT_PACKAGE);
	}

	public boolean isDeleteable() {
		return getLocation().startsWith("/Repository/");
	}

	protected BundleManifest getManifest() {
		if (manifest == null) {
			manifest = BundleManifestFactory.createBundleManifest(bundle.getHeaders());
		}
		return manifest;
	}

	public List<TemplateImportedPackage> getImportedPackages() {
		final List<TemplateImportedPackage> packages = new ArrayList<TemplateImportedPackage>();
		for (final ImportedPackage importedPackage : getManifest().getImportPackage().getImportedPackages()) {
			final TemplateImportedPackage bundlePackage = new TemplateImportedPackage();
			bundlePackage.setName(importedPackage.getPackageName());
			final Version ceiling = importedPackage.getVersion().getCeiling();
			if (ceiling != null) {
				bundlePackage.setMaxVersion(ceiling.toString());
			}
			final Version floor = importedPackage.getVersion().getFloor();
			if (floor != null) {
				bundlePackage.setMinVersion(floor.toString());
			}
			packages.add(bundlePackage);
		}
		return packages;
	}

	public List<ExportedPackage> getExportedPackages() {
		return getManifest().getExportPackage().getExportedPackages();
	}

	/* Utility operations */

	@Override
	public int compareTo(final TemplateBundle other) {
		if (this.getBundleId() == FRAMEWORK_BUNDLE_ID) {
			return Integer.MIN_VALUE;
		} else if (other.getBundleId() == 0) {
			return Integer.MAX_VALUE;
		}
		final int compare = this.getName().compareToIgnoreCase(other.getName());
		if (compare == 0) {
			return this.getVersion().compareTo(other.getVersion());

		}
		return compare;
	}

	/* Delegate operations */

}