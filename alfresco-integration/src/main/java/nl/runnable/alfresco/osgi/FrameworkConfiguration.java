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

package nl.runnable.alfresco.osgi;

import static org.osgi.framework.Constants.*;

import java.io.File;
import java.util.*;

import org.osgi.framework.launch.Framework;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/**
 * Value object representing {@link Framework} configuration.
 * 
 * @author Laurens Fridael
 * 
 */
public class FrameworkConfiguration {

	/* Configuration */

	private File storageDirectory;

	private boolean flushBundleCacheOnFirstInit = false;

	private Set<SystemPackage> coreSystemPackages = Collections.emptySet();

	private Set<SystemPackage> additionalSystemPackages = Collections.emptySet();

	/* Main operations */

	/**
	 * Converts this configuration to a Map suitable for passing to <code>FrameworkFactory.newFramework(Map)</code>.
	 * 
	 * @return The configuration as a Map.
	 */
	public Map<String, String> toMap() {
		final Map<String, String> configuration = new HashMap<String, String>();
		if (getStorageDirectory() != null) {
			configuration.put(FRAMEWORK_STORAGE, getStorageDirectory().getAbsolutePath());
		}
		if (isFlushBundleCacheOnFirstInit()) {
			configuration.put(FRAMEWORK_STORAGE_CLEAN, FRAMEWORK_STORAGE_CLEAN_ONFIRSTINIT);
		}
		if (CollectionUtils.isEmpty(getCoreSystemPackages()) == false) {
			configuration.put(FRAMEWORK_SYSTEMPACKAGES, createSystemPackagesConfiguration(getCoreSystemPackages()));
		}
		if (CollectionUtils.isEmpty(getAdditionalSystemPackages()) == false) {
			configuration.put(FRAMEWORK_SYSTEMPACKAGES_EXTRA,
					createSystemPackagesConfiguration(getAdditionalSystemPackages()));
		}
		return configuration;
	}

	/* Utility operations */

	protected String createSystemPackagesConfiguration(final Set<SystemPackage> systemPackages) {
		final StringBuilder sb = new StringBuilder();
		for (final Iterator<SystemPackage> it = systemPackages.iterator(); it.hasNext();) {
			final SystemPackage systemPackage = it.next();
			String version = systemPackage.getVersion();
			if (version == null) {
				// TODO: find out if specifying a version for a system package is mandatory.
				version = SystemPackage.DEFAULT_VERSION;
			}
			sb.append(systemPackage.getName()).append(";version=").append(version);
			if (it.hasNext()) {
				sb.append(",");
			}
		}
		return sb.toString();
	}

	/* Configuration */

	public File getStorageDirectory() {
		return storageDirectory;
	}

	public void setStorageDirectory(final File storageDirectory) {
		this.storageDirectory = storageDirectory;
	}

	public boolean isFlushBundleCacheOnFirstInit() {
		return flushBundleCacheOnFirstInit;
	}

	public void setFlushBundleCacheOnFirstInit(final boolean flushBundleCacheOnFirstInit) {
		this.flushBundleCacheOnFirstInit = flushBundleCacheOnFirstInit;
	}

	public Set<SystemPackage> getCoreSystemPackages() {
		return coreSystemPackages;
	}

	public void setCoreSystemPackages(final Set<SystemPackage> coreSystemPackages) {
		Assert.notNull(coreSystemPackages);
		this.coreSystemPackages = coreSystemPackages;
	}

	public Set<SystemPackage> getAdditionalSystemPackages() {
		return additionalSystemPackages;
	}

	public void setAdditionalSystemPackages(final Set<SystemPackage> hostPackages) {
		Assert.notNull(hostPackages);
		this.additionalSystemPackages = hostPackages;
	}

}
