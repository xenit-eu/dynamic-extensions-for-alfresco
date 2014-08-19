package com.github.dynamicextensionsalfresco.osgi;

import org.springframework.util.Assert;

import java.io.File;

/**
 * Value object representing the OSGi container configuration;
 * 
 * @author Laurens Fridael
 * 
 */
public class Configuration {
    public static final String SYSTEM_PACKAGE_CACHE_FILENAME = "system-packages.txt";

	private boolean frameworkRestartEnabled = true;

	private boolean hotDeployEnabled = true;

	private boolean repositoryBundlesEnabled = true;

	private File storageDirectory;

	private PackageCacheMode systemPackageCacheMode;

	public boolean isFrameworkRestartEnabled() {
		return frameworkRestartEnabled;
	}

	public void setFrameworkRestartEnabled(boolean frameworkRestartEnabled) {
		this.frameworkRestartEnabled = frameworkRestartEnabled;
	}

	public boolean isHotDeployEnabled() {
		return hotDeployEnabled;
	}

	public void setHotDeployEnabled(boolean hotDeployEnabled) {
		this.hotDeployEnabled = hotDeployEnabled;
	}

	public boolean isRepositoryBundlesEnabled() {
		return repositoryBundlesEnabled;
	}

	public void setRepositoryBundlesEnabled(boolean repositoryBundlesEnabled) {
		this.repositoryBundlesEnabled = repositoryBundlesEnabled;
	}

	public File getStorageDirectory() {
		return storageDirectory;
	}

	public void setStorageDirectory(final File storageDirectory) {
		Assert.notNull(storageDirectory);
		this.storageDirectory = storageDirectory;
	}

	public void setSystemPackageCacheMode(PackageCacheMode systemPackageCacheMode) {
		this.systemPackageCacheMode = systemPackageCacheMode;
	}

	public PackageCacheMode getSystemPackageCacheMode() {
		return systemPackageCacheMode;
	}

    public File getSystemPackageCache() {
        return new File(System.getProperty("java.io.tmpdir"), SYSTEM_PACKAGE_CACHE_FILENAME);
    }
}
