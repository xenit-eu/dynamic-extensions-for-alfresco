package nl.runnable.alfresco.extensions.controlpanel.template;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
 * Adapts an {@link Bundle} for display in a Freemarker template.
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
		return toString(bundle.getHeaders().get(Constants.BUNDLE_NAME));
	}

	public String getDescription() {
		return toString(bundle.getHeaders().get(Constants.BUNDLE_DESCRIPTION));
	}

	public boolean isDynamicExtension() {
		return BundleHelper.isDynamicExtension(bundle);
	}

	public String getLocation() {
		return bundle.getLocation();
	}

	public String getLastModified() {
		final long lastModified = bundle.getLastModified();
		if (lastModified > 0) {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z").format(new Date(lastModified));
		} else {
			return null;
		}
	}

	public String getVersion() {
		return bundle.getVersion().toString();
	}

	public String getStore() {
		if (bundle.getLocation().startsWith("file:")) {
			return "filesystem";
		} else if (bundle.getLocation().startsWith("/")) {
			return "repository";
		} else {
			return "n/a";
		}
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

	public Object getExportPackage() {
		return bundle.getHeaders().get(Constants.EXPORT_PACKAGE);
	}

	public boolean isDeleteable() {
		return getLocation().startsWith("/Company Home");
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

	/**
	 * Utility function for working around problems when compiling against JDK 7. See <a
	 * href="https://mail.osgi.org/pipermail/osgi-dev/2011-August/003223.html">this page</a> for an explanation on the
	 * JDK 7 compile issue.
	 * 
	 * @param value
	 * @return
	 */
	private static String toString(final Object value) {
		return value != null ? value.toString() : null;
	}

}
