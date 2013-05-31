package nl.runnable.alfresco.controlpanel.impl;

import org.osgi.framework.Version;

class BundleIdentifier {

	static BundleIdentifier fromSymbolicNameAndVersion(final String symbolicName, final String version) {
		return new BundleIdentifier(symbolicName, new Version(version));
	}

	private final String symbolicName;

	private final Version version;

	private BundleIdentifier(final String symbolicName, final Version version) {
		this.symbolicName = symbolicName;
		this.version = version;
	}

	public String getSymbolicName() {
		return symbolicName;
	}

	public Version getVersion() {
		return version;
	}

	public String toJarFilename() {
		return String.format("%s-%s.jar", getSymbolicName(), getVersion().toString());
	}

}
