package com.github.dynamicextensionsalfresco.osgi;

import static org.osgi.framework.Constants.*;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.osgi.framework.launch.Framework;
import org.springframework.util.Assert;

/**
 * Value object representing {@link Framework} configuration.
 * 
 * @author Laurens Fridael
 * 
 */
public class FrameworkConfiguration {

	/* Configuration */

	private Configuration configuration;

	private boolean flushBundleCacheOnFirstInit = true;

	private ConfigurationValues<SystemPackage> coreSystemPackages = null;

	private ConfigurationValues<SystemPackage> additionalSystemPackages = null;

	/* Main operations */

	/**
	 * Converts this configuration to a Map suitable for passing to <code>FrameworkFactory.newFramework(Map)</code>.
	 * 
	 * @return The configuration as a Map.
	 */
	public Map<String, String> toMap() {
		final Map<String, String> configuration = new HashMap<String, String>();
		if (getStorageDirectory() != null) {
			configuration.put(FRAMEWORK_STORAGE, getStorageDirectory().getAbsolutePath());
		}
		if (isFlushBundleCacheOnFirstInit()) {
			configuration.put(FRAMEWORK_STORAGE_CLEAN, FRAMEWORK_STORAGE_CLEAN_ONFIRSTINIT);
		}
		if (getCoreSystemPackages() != null) {
			configuration.put(FRAMEWORK_SYSTEMPACKAGES, createSystemPackagesConfiguration(getCoreSystemPackages()));
		}
		if (getAdditionalSystemPackages() != null) {
			configuration.put(FRAMEWORK_SYSTEMPACKAGES_EXTRA,
					createSystemPackagesConfiguration(getAdditionalSystemPackages()));
		}
		return configuration;
	}

	/* Utility operations */

	protected String createSystemPackagesConfiguration(final ConfigurationValues<SystemPackage> systemPackages) {
		final StringBuilder sb = new StringBuilder();
		for (final Iterator<SystemPackage> it = systemPackages.iterator(); it.hasNext();) {
			final SystemPackage systemPackage = it.next();
			String version = systemPackage.getVersion();
			if (version == null) {
				// TODO: find out if specifying a version for a system package is mandatory.
				version = SystemPackage.DEFAULT_VERSION;
			}
			sb.append(systemPackage.getName()).append(";version=").append(version);
			if (it.hasNext()) {
				sb.append(",");
			}
		}
		return sb.toString();
	}

	/* Configuration */

	public void setConfiguration(final Configuration configuration) {
		Assert.notNull(configuration);
		this.configuration = configuration;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public File getStorageDirectory() {
		return getConfiguration().getStorageDirectory();
	}

	public boolean isFlushBundleCacheOnFirstInit() {
		return flushBundleCacheOnFirstInit;
	}

	public void setFlushBundleCacheOnFirstInit(final boolean flushBundleCacheOnFirstInit) {
		this.flushBundleCacheOnFirstInit = flushBundleCacheOnFirstInit;
	}

	public void setCoreSystemPackages(final ConfigurationValues<SystemPackage> coreSystemPackages) {
		Assert.notNull(coreSystemPackages);
		this.coreSystemPackages = coreSystemPackages;
	}

	public ConfigurationValues<SystemPackage> getCoreSystemPackages() {
		return coreSystemPackages;
	}

	public void setAdditionalSystemPackages(final ConfigurationValues<SystemPackage> additionalSystemPackages) {
		Assert.notNull(additionalSystemPackages);
		this.additionalSystemPackages = additionalSystemPackages;
	}

	public ConfigurationValues<SystemPackage> getAdditionalSystemPackages() {
		return additionalSystemPackages;
	}

}
