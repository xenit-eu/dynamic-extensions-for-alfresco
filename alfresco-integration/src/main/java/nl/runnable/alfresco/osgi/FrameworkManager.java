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

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import nl.runnable.alfresco.metadata.ContainerMetadata;
import nl.runnable.alfresco.metadata.MetadataRegistry;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.launch.Framework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Manages a {@link Framework}'s lifecycle. It taken care of initializing and destroying the Framework and
 * (un)registering services and {@link BundleListener}s.
 * 
 * @author Laurens Fridael
 * 
 */
@Service
public class FrameworkManager implements ResourceLoaderAware {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	/* Dependencies */

	private Framework framework;

	private List<BundleContextRegistrar> bundleContextRegistrars = Collections.emptyList();

	private final List<ServiceRegistration<?>> serviceRegistrations = new ArrayList<ServiceRegistration<?>>();

	private ResourcePatternResolver resourcePatternResolver;

	private final BundleHelper bundleHelper = new BundleHelper();

	private FileInstallConfigurer fileInstallConfigurer;

	private MetadataRegistry metadataRegistry;

	/* Configuration */

	private List<BundleListener> bundleListeners = Collections.emptyList();

	private List<ServiceListener> serviceListeners = Collections.emptyList();

	private String blueprintBundlesLocation;

	private String fileInstallBundlesLocation;

	private String standardBundlesLocation;

	private boolean fileInstallEnabled = true;

	/**
	 * Starts the {@link Framework} and registers services and {@link BundleListener}s.
	 * 
	 * @throws BundleException
	 */
	public void initialize() {
		startFramework();
		registerFrameworkMetadata();
		final List<Bundle> coreBundles = installCoreBundles();
		registerServices();
		startBundles(coreBundles);
		registerCoreBundleMetadata(coreBundles);
		if (isFileInstallEnabled() == false) {
			final List<Bundle> extensionBundles = installExtensionBundles();
			startBundles(extensionBundles);
		}
		registerBundleListeners();
		registerServiceListeners();
	}

	protected void startFramework() {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Starting Framework.");
			}
			getFramework().start();
		} catch (final BundleException e) {
			logger.error("Could not start Framework.", e);
		}
	}

	/**
	 * Installs the Bundles that make up the core of the framework. These bundles are started before any extension
	 * bundles.
	 * <p>
	 * The core bundles consist of:
	 * <ul>
	 * <li>Gemini Blueprint
	 * <li>File Install
	 * <li>Any additional standard bundles configured through {@link #setStandardBundlesLocation(String)}. (Currently
	 * contains the Jackson JSON library.)
	 * </ul>
	 * 
	 * @return
	 */
	protected List<Bundle> installCoreBundles() {
		final List<Bundle> bundles = new ArrayList<Bundle>();
		try {
			final List<String> locationPatterns = new ArrayList<String>();
			locationPatterns.add(getBlueprintBundlesLocation());
			if (isFileInstallEnabled() && StringUtils.hasText(getFileInstallBundlesLocation())) {
				locationPatterns.add(getFileInstallBundlesLocation());
			}
			if (StringUtils.hasText(getStandardBundlesLocation())) {
				locationPatterns.add(getStandardBundlesLocation());
			}
			for (final String locationPattern : locationPatterns) {
				try {
					for (final Resource bundleResource : getResourcePatternResolver().getResources(locationPattern)) {
						final String location = bundleResource.getURI().toString();
						if (logger.isDebugEnabled()) {
							logger.debug("Installing Bundle: {}", location);
						}
						try {
							final Bundle bundle = getFramework().getBundleContext().installBundle(location,
									bundleResource.getInputStream());
							bundles.add(bundle);
						} catch (final BundleException e) {
							logger.error("Error installing Bundle: {}", e);
						}
					}
				} catch (final FileNotFoundException e) {
					logger.warn("Could not find Bundles at location '{}'.", locationPattern);
				}
			}
		} catch (final IOException e) {
			throw new RuntimeException("Error installing core Bundles: " + e.getMessage(), e);
		}
		return bundles;

	}

	protected List<Bundle> installExtensionBundles() {
		final List<Bundle> bundles = new ArrayList<Bundle>();
		for (final String directory : getFileInstallConfigurer().getDirectoriesAsAbsolutePaths()) {
			final File dir = new File(directory);
			if (dir.isDirectory() == false) {
				if (logger.isWarnEnabled()) {
					logger.warn("Directory does not exist: {}", dir.getAbsolutePath());
				}
				continue;
			}
			final File[] jarFiles = dir.listFiles(new FileFilter() {

				@Override
				public boolean accept(final File file) {
					return file.getName().toLowerCase().endsWith(".jar");
				}
			});
			for (final File jarFile : jarFiles) {
				try {
					final String location = jarFile.toURI().toString();
					if (logger.isDebugEnabled()) {
						logger.debug("Installing Bundle: {}", location);
					}
					final Bundle bundle = getFramework().getBundleContext().installBundle(location,
							new FileInputStream(jarFile));
					bundles.add(bundle);
				} catch (final BundleException e) {
					logger.warn("Error installing Bundle: {}", e);
				} catch (final IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return bundles;
	}

	/**
	 * Registers the {@link Framework} with the {@link MetadataRegistry}.
	 */
	protected void registerFrameworkMetadata() {
		getMetadataRegistry().registerCoreBundle(getFramework().getBundleId());
		final ContainerMetadata containerMetadata = getMetadataRegistry().getContainerMetadata();
		containerMetadata.setFrameworkBundleId(getFramework().getBundleId());
		containerMetadata.setFileInstallPaths(fileInstallConfigurer.getDirectoriesAsAbsolutePaths());
	}

	/**
	 * Registers the given core Bundles with the {@link MetadataRegistry}.
	 * 
	 * @param coreBundles
	 */
	protected void registerCoreBundleMetadata(final List<Bundle> coreBundles) {
		for (final Bundle coreBundle : coreBundles) {
			getMetadataRegistry().registerCoreBundle(coreBundle.getBundleId());
		}
	}

	protected void startBundles(final List<Bundle> bundles) {
		for (final Bundle bundle : bundles) {
			if (getBundleHelper().isFragmentBundle(bundle) == false) {
				if (logger.isDebugEnabled()) {
					logger.debug("Starting Bundle {}", bundle.getBundleId());
				}
				getBundleHelper().startBundle(bundle);
			}
		}
	}

	protected void registerServices() {
		if (logger.isDebugEnabled()) {
			logger.debug("Registering services.");
		}
		for (final BundleContextRegistrar bundleContextRegistrar : getBundleContextRegistrars()) {
			final List<ServiceRegistration<?>> servicesRegistered = bundleContextRegistrar
					.registerInBundleContext(getFramework().getBundleContext());
			getServiceRegistrations().addAll(servicesRegistered);
		}
	}

	protected void registerBundleListeners() {
		for (final BundleListener bundleListener : getBundleListeners()) {
			getFramework().getBundleContext().addBundleListener(bundleListener);
		}
	}

	protected void registerServiceListeners() {
		for (final ServiceListener serviceListener : getServiceListeners()) {
			getFramework().getBundleContext().addServiceListener(serviceListener);
		}
	}

	/**
	 * 
	 * Unregisters services and {@link BundleListener}s and stops the {@link Framework}.
	 */
	public void destroy() {
		unregisterServiceListeners();
		unregisterBundleListeners();
		unregisterServices();
		stopFramework();
	}

	protected void unregisterServiceListeners() {
		for (final ServiceListener serviceListener : getServiceListeners()) {
			getFramework().getBundleContext().removeServiceListener(serviceListener);
		}
	}

	protected void unregisterBundleListeners() {
		for (final BundleListener bundleListener : getBundleListeners()) {
			getFramework().getBundleContext().removeBundleListener(bundleListener);
		}
	}

	protected void unregisterServices() {
		for (final Iterator<ServiceRegistration<?>> it = getServiceRegistrations().iterator(); it.hasNext();) {
			try {
				final ServiceRegistration<?> serviceRegistration = it.next();
				if (logger.isDebugEnabled()) {
					logger.debug("Unregistering service {}", serviceRegistration.getReference());
				}
				serviceRegistration.unregister();
			} catch (final RuntimeException e) {
				logger.warn("Error unregistering service.", e.getMessage());
			} finally {
				it.remove();
			}
		}
	}

	protected void stopFramework() {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Stopping Framework.");
			}
			getFramework().stop();
		} catch (final BundleException e) {
			logger.error("Could not stop Framework.", e);
		}
	}

	/* Dependencies */

	@Required
	public void setFramework(final Framework framework) {
		Assert.notNull(framework, "Framework cannot be null.");
		this.framework = framework;
	}

	protected Framework getFramework() {
		return framework;
	}

	public void setBundleContextRegistrars(final List<BundleContextRegistrar> bundleContextRegistrars) {
		Assert.notNull(bundleContextRegistrars);
		this.bundleContextRegistrars = bundleContextRegistrars;
	}

	protected List<BundleContextRegistrar> getBundleContextRegistrars() {
		return bundleContextRegistrars;
	}

	protected List<ServiceRegistration<?>> getServiceRegistrations() {
		return serviceRegistrations;
	}

	@Override
	public void setResourceLoader(final ResourceLoader resourceLoader) {
		Assert.isInstanceOf(ResourcePatternResolver.class, resourceLoader);
		this.resourcePatternResolver = (ResourcePatternResolver) resourceLoader;
	}

	protected ResourcePatternResolver getResourcePatternResolver() {
		return resourcePatternResolver;
	}

	public void setFileInstallConfigurer(final FileInstallConfigurer fileInstallConfigurer) {
		this.fileInstallConfigurer = fileInstallConfigurer;
	}

	protected FileInstallConfigurer getFileInstallConfigurer() {
		return fileInstallConfigurer;
	}

	public void setMetadataRegistry(final MetadataRegistry metadataRegistry) {
		this.metadataRegistry = metadataRegistry;
	}

	protected MetadataRegistry getMetadataRegistry() {
		return metadataRegistry;
	}

	/* Configuration */

	protected BundleHelper getBundleHelper() {
		return bundleHelper;
	}

	public void setBundleListeners(final List<BundleListener> bundleListeners) {
		Assert.notNull(bundleListeners);
		this.bundleListeners = bundleListeners;
	}

	protected List<BundleListener> getBundleListeners() {
		return bundleListeners;
	}

	public void setServiceListeners(final List<ServiceListener> serviceListeners) {
		Assert.notNull(serviceListeners);
		this.serviceListeners = serviceListeners;
	}

	protected List<ServiceListener> getServiceListeners() {
		return serviceListeners;
	}

	@Required
	public void setBlueprintBundlesLocation(final String blueprintBundleLocation) {
		Assert.hasText(blueprintBundleLocation);
		this.blueprintBundlesLocation = blueprintBundleLocation;
	}

	protected String getBlueprintBundlesLocation() {
		return blueprintBundlesLocation;
	}

	public void setFileInstallBundlesLocation(final String fileInstallBundlesLocation) {
		this.fileInstallBundlesLocation = fileInstallBundlesLocation;
	}

	protected String getFileInstallBundlesLocation() {
		return fileInstallBundlesLocation;
	}

	public void setStandardBundlesLocation(final String standardBundlesLocation) {
		this.standardBundlesLocation = standardBundlesLocation;
	}

	protected String getStandardBundlesLocation() {
		return standardBundlesLocation;
	}

	public void setFileInstallEnabled(final boolean installFileInstallBundles) {
		this.fileInstallEnabled = installFileInstallBundles;
	}

	protected boolean isFileInstallEnabled() {
		return fileInstallEnabled;
	}

}
