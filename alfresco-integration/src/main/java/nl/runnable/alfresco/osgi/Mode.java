package nl.runnable.alfresco.osgi;

/**
 * Enum representing the mode to run the OSGi container in.
 * <p>
 * <strong>DEVELOPMENT</strong>
 * <ul>
 * <li>Bundles can be installed and uninstalled at runtime.</li>
 * <li>OSGi Framework can be restarted.</li>
 * </ul>
 * <p>
 * <strong>PRODUCTION</strong>
 * <ul>
 * <li>Bundles are installed during startup only, runtime installation is disabled.</li>
 * <li>OSGi Framework cannot be restarted.</li>
 * </ul>
 * 
 * @author Laurens Fridael
 * 
 */
public enum Mode {

	DEVELOPMENT(true, true), PRODUCTION(false, false);

	private boolean frameworkRestartEnabled;

	private boolean bundleInstallEnabled;

	private Mode(final boolean frameworkRestartEnabled, final boolean bundleInstallEnabled) {
		this.frameworkRestartEnabled = frameworkRestartEnabled;
		this.bundleInstallEnabled = bundleInstallEnabled;
	}

	public boolean isFrameworkRestartEnabled() {
		return frameworkRestartEnabled;
	}

	public boolean isBundleInstallEnabled() {
		return bundleInstallEnabled;
	}

}
