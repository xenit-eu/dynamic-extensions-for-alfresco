package com.github.dynamicextensionsalfresco.osgi;

import java.io.File;

/**
 * Value object representing the OSGi container configuration;
 */
public class Configuration {
    private static final String TMP_DIR = "java.io.tmpdir";

    private boolean frameworkRestartEnabled = true;
    public boolean getFrameworkRestartEnabled() { return frameworkRestartEnabled; }
    public void setFrameworkRestartEnabled(boolean frameworkRestartEnabled) {
        this.frameworkRestartEnabled = frameworkRestartEnabled;
    }

    private boolean hotDeployEnabled = true;
    public boolean getHotDeployEnabled() { return hotDeployEnabled; }
    public void setHotDeployEnabled(boolean hotDeployEnabled) {
        this.hotDeployEnabled = hotDeployEnabled;
    }

    private boolean repositoryBundlesEnabled = true;
    public boolean getRepositoryBundlesEnabled() { return repositoryBundlesEnabled; }
    public void setRepositoryBundlesEnabled(boolean repositoryBundlesEnabled) {
        this.repositoryBundlesEnabled = repositoryBundlesEnabled;
    }

    private File storageDirectory = null;
    public File getStorageDirectory() {
        if(storageDirectory == null) {
            storageDirectory = createTempFile("bundles");
        }

        return this.storageDirectory;
    }
    public void setStorageDirectory(File storageDirectory) {
        this.storageDirectory = storageDirectory;
    }

    private PackageCacheMode systemPackageCacheMode = null;
    public PackageCacheMode getSystemPackageCacheMode() {
        return this.systemPackageCacheMode;
    }
    public void setSystemPackageCacheMode(PackageCacheMode packageCacheMode) {
        this.systemPackageCacheMode = packageCacheMode;
    }

    private File systemPackageCache = null;
    public File getSystemPackageCache() {
        if(systemPackageCache == null) {
            systemPackageCache = createTempFile("system-packages.txt");
        }

        return this.systemPackageCache;
    }

    private static File createTempFile(String child) {
        return new File(System.getProperty(TMP_DIR), child);
    }
}
