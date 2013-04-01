package nl.runnable.alfresco.osgi;

import java.util.List;

public class Configuration {

	private List<String> fileInstallPaths;

	public void setFileInstallPaths(final List<String> fileInstallPaths) {
		this.fileInstallPaths = fileInstallPaths;
	}

	public List<String> getFileInstallPaths() {
		return fileInstallPaths;
	}
}
