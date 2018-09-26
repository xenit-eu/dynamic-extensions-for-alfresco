package com.github.dynamicextensionsalfresco.osgi.spring;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.LinkedHashSet;
import java.util.Set;

import com.github.dynamicextensionsalfresco.osgi.SystemPackage;
import com.github.dynamicextensionsalfresco.osgi.SystemPackageEditor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

/**
 * {@link FactoryBean} for creating a List of {@link SystemPackage}s from a text file.
 * 
 * @author Laurens Fridael
 * 
 */
public class SystemPackageConfigurationFactoryBean extends AbstractConfigurationFileFactoryBean<Set<SystemPackage>> {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	/* Configuration */

	private String defaultVersion;

	/* State */

	private Set<SystemPackage> systemPackages;

	/* Operations */

	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends Set<SystemPackage>> getObjectType() {
		return (Class<? extends Set<SystemPackage>>) (Class<?>) Set.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public Set<SystemPackage> getObject() throws IOException {
		if (systemPackages == null) {
			systemPackages = createSystemPackages();
		}
		return systemPackages;
	}

	/* Utility operations */

	protected Set<SystemPackage> createSystemPackages() throws IOException {
		final Set<SystemPackage> systemPackages = new LinkedHashSet<SystemPackage>();
		final SystemPackageEditor systemPackageEditor = new SystemPackageEditor();
		systemPackageEditor.setDefaultVersion(getDefaultVersion());
		for (final Resource configuration : resolveConfigurations()) {
			LineNumberReader in = null;
			try {
				in = new LineNumberReader(new InputStreamReader(configuration.getInputStream()));
				for (String line; (line = in.readLine()) != null;) {
					line = line.trim();
					if (line.isEmpty() || line.startsWith("#")) {
						// Skip empty lines and comments.
						continue;
					}
					try {
						systemPackageEditor.setAsText(line);
						final SystemPackage systemPackage = (SystemPackage) systemPackageEditor.getValue();
						systemPackages.add(systemPackage);
					} catch (final IllegalArgumentException e) {
						logger.warn("Could not parse SystemPackage configuration line: {}", e.getMessage());
					}
				}
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (final IOException ignore) {}
				}
			}
		}
		return systemPackages;
	}

	/* Configuration */

	public void setDefaultVersion(final String defaultVersion) {
		Assert.hasText(defaultVersion);
		this.defaultVersion = defaultVersion;
	}

	public String getDefaultVersion() {
		return defaultVersion;
	}

}
