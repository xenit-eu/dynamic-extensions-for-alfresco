/*
Copyright (c) 2012, Runnable
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
 * Neither the name of Runnable nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package nl.runnable.alfresco.osgi.spring;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

import nl.runnable.alfresco.osgi.SystemPackage;
import nl.runnable.alfresco.osgi.SystemPackageEditor;

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
public class SystemPackageConfigurationFactoryBean extends AbstractConfigurationFileFactoryBean<List<SystemPackage>> {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	/* Configuration */

	private String defaultVersion = "1.0";

	/* State */

	private List<SystemPackage> systemPackages;

	/* Operations */

	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends List<SystemPackage>> getObjectType() {
		return (Class<? extends List<SystemPackage>>) (Class<?>) List.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public List<SystemPackage> getObject() throws IOException {
		if (systemPackages == null) {
			systemPackages = createSystemPackages();
		}
		return systemPackages;
	}

	/* Utility operations */

	protected List<SystemPackage> createSystemPackages() throws IOException {
		final List<SystemPackage> systemPackages = new ArrayList<SystemPackage>();
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
					} catch (final IOException e) {
					}
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
