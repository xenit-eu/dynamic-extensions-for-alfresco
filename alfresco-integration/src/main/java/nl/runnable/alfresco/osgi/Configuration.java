package nl.runnable.alfresco.osgi;

import java.io.File;

import org.springframework.util.Assert;

/**
 * Value object representing the OSGi container configuration;
 * 
 * @author Laurens Fridael
 * 
 */
public class Configuration {

	private Mode mode = Mode.DEVELOPMENT;

	private File bundleDirectory;

	private File storageDirectory;

	public Mode getMode() {
		return mode;
	}

	public void setMode(final Mode mode) {
		Assert.notNull(mode);
		this.mode = mode;
	}

	public File getBundleDirectory() {
		return bundleDirectory;
	}

	public void setBundleDirectory(final File bundleDirectory) {
		Assert.notNull(bundleDirectory);
		this.bundleDirectory = bundleDirectory;
	}

	public File getStorageDirectory() {
		return storageDirectory;
	}

	public void setStorageDirectory(final File storageDirectory) {
		Assert.notNull(storageDirectory);
		this.storageDirectory = storageDirectory;
	}

}
