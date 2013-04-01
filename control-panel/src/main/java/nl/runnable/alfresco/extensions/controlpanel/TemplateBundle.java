package nl.runnable.alfresco.extensions.controlpanel;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.springframework.util.Assert;

import com.springsource.util.osgi.manifest.BundleManifest;
import com.springsource.util.osgi.manifest.BundleManifestFactory;

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

	TemplateBundle(final Bundle bundle) {
		Assert.notNull(bundle);
		this.bundle = bundle;
	}

	public long getId() {
		return bundle.getBundleId();
	}

	public String getSymbolicName() {
		return bundle.getSymbolicName();
	}

	public String getVersion() {
		return bundle.getVersion().toString();
	}

	public String getName() {
		return bundle.getHeaders().get(Constants.BUNDLE_NAME);
	}

	public String getState() {
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

	public BundleManifest getManifest() {
		if (manifest == null) {
			manifest = BundleManifestFactory.createBundleManifest(bundle.getHeaders());
		}
		return manifest;
	}

	@Override
	public int compareTo(final TemplateBundle other) {
		if (this.getId() == FRAMEWORK_BUNDLE_ID) {
			return Integer.MIN_VALUE;
		} else if (other.getId() == 0) {
			return Integer.MAX_VALUE;
		}
		final int compare = this.getName().compareToIgnoreCase(other.getName());
		if (compare == 0) {
			return this.getVersion().compareToIgnoreCase(other.getVersion());

		}
		return compare;
	}

}
