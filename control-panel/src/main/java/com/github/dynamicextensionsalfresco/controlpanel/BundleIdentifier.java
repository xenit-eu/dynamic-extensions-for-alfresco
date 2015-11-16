package com.github.dynamicextensionsalfresco.controlpanel;

import org.osgi.framework.Version;

public class BundleIdentifier {

	public static BundleIdentifier fromSymbolicNameAndVersion(final String symbolicName, final String version) {
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
		return String.format("%s.jar", getSymbolicName());
	}

    @Override
    public String toString() {
        return "BundleIdentifier{" +
            "symbolicName='" + symbolicName + '\'' +
            ", version=" + version +
            '}';
    }
}
