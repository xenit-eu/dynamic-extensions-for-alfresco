package nl.runnable.alfresco.extensions.impl;

/**
 * Value Object holding description for Dynamic Extensions folders.
 * 
 * @author Laurens Fridael
 * 
 */
public class Descriptions {

	private String baseFolder;

	private String bundleFolder;

	private String configurationFolder;

	private String installationHistoryFolder;

	public String getBaseFolder() {
		return baseFolder;
	}

	public void setBaseFolder(final String baseFolder) {
		this.baseFolder = baseFolder;
	}

	public String getBundleFolder() {
		return bundleFolder;
	}

	public void setBundleFolder(final String extensionsFolder) {
		this.bundleFolder = extensionsFolder;
	}

	public String getConfigurationFolder() {
		return configurationFolder;
	}

	public void setConfigurationFolder(final String configurationFolder) {
		this.configurationFolder = configurationFolder;
	}

	public String getInstallationHistoryFolder() {
		return installationHistoryFolder;
	}

	public void setInstallationHistoryFolder(final String installationHistoryFolder) {
		this.installationHistoryFolder = installationHistoryFolder;
	}

}
