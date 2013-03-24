package nl.runnable.alfresco.extensions;

import java.util.List;

/**
 * Provides metadata on the Dynamic Extensions container.
 * 
 * @author Laurens Fridael
 * 
 */
public class ContainerMetadata {

	private long frameworkBundleId;

	private List<String> fileInstallPaths;

	public void setFrameworkBundleId(final long frameworkBundleId) {
		this.frameworkBundleId = frameworkBundleId;
	}

	public long getFrameworkBundleId() {
		return frameworkBundleId;
	}

	public void setFileInstallPaths(final List<String> fileInstallPaths) {
		this.fileInstallPaths = fileInstallPaths;
	}

	public List<String> getFileInstallPaths() {
		return fileInstallPaths;
	}
}
