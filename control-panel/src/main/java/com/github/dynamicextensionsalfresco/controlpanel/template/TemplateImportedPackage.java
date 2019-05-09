package com.github.dynamicextensionsalfresco.controlpanel.template;

import com.springsource.util.osgi.manifest.ExportedPackage;
import com.springsource.util.osgi.manifest.ImportedPackage;

/**
 * Represents a Java package imported by an OSGi bundle.
 * <p>
 * This class exists to work around visibility issues when Freemarker accesses properties of {@link ImportedPackage} and
 * {@link ExportedPackage} instances using JavaBeans conventions. Parts of the implementations of these interfaces are
 * package private, which means they cannot be accessed using JavaBeans reflection.
 * 
 * @author Laurens Fridael
 * 
 */
public class TemplateImportedPackage {

	private String name;

	private String minVersion;

	private String maxVersion;

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getMinVersion() {
		return minVersion;
	}

	public void setMinVersion(final String minVersion) {
		this.minVersion = minVersion;
	}

	public String getMaxVersion() {
		return maxVersion;
	}

	public void setMaxVersion(final String maxVersion) {
		this.maxVersion = maxVersion;
	}

}
