package com.github.dynamicextensionsalfresco.osgi;

import org.osgi.framework.launch.Framework;
import org.springframework.util.Assert;

/**
 * Represents a {@link Framework} system package.
 * 
 * @author Laurens Fridael
 * @see FrameworkConfiguration#setCoreSystemPackages(ConfigurationValues)
 * @see FrameworkConfiguration#setAdditionalSystemPackages(ConfigurationValues)
 */
public class SystemPackage {

	public static final String DEFAULT_VERSION = "1.0";

	public static SystemPackage fromString(final String line) {
		final String[] tokens = line.split(";");
		if (tokens.length > 1) {
			return new SystemPackage(tokens[0], tokens[1]);
		} else {
			return new SystemPackage(tokens[0], null);
		}
	}

	private final String name;

	private final String version;

	public SystemPackage(final String name, final String version) {
		Assert.hasText(name, "Name cannot be empty.");
		this.name = name;
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	@Override
	public String toString() {
		if (version != null) {
			return String.format("%s;%s", name, version);
		} else {
			return name;
		}
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		final SystemPackage that = (SystemPackage) o;

		return name.equals(that.name);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}
}
