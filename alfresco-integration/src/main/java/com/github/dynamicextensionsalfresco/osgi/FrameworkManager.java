package com.github.dynamicextensionsalfresco.osgi;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.osgi.framework.*;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.wiring.FrameworkWiring;
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

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

	private RepositoryStoreService repositoryStoreService;

	private ContentService contentService;

	/* Configuration */

	private Configuration configuration;

	private String blueprintBundlesLocation;

	private String standardBundlesLocation;

	private String customBundlesLocation;

	/**
	 * Starts the {@link Framework} and registers services and {@link BundleListener}s.
	 * 
	 * @throws BundleException
	 */
	public void initialize() {
		startFramework();
		registerServices();
		final List<Bundle> bundles = new ArrayList<Bundle>();
		bundles.addAll(installCoreBundles());
		if (isRepositoryInstallEnabled()) {
			bundles.addAll(installRepositoryBundles());
		}
		startBundles(bundles);
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
	 * <li>File Install (optional, can be disabled)
	 * <li>Any additional standard bundles configured through {@link #setStandardBundlesLocation(String)}.
	 * </ul>
	 * 
	 * @return
	 */
	protected List<Bundle> installCoreBundles() {
		final List<Bundle> bundles = new ArrayList<Bundle>();
		try {
			final List<String> locationPatterns = new ArrayList<String>();
			locationPatterns.add(getBlueprintBundlesLocation());
			if (StringUtils.hasText(getStandardBundlesLocation())) {
				locationPatterns.add(getStandardBundlesLocation());
			}
			if (StringUtils.hasText(getCustomBundlesLocation())) {
				locationPatterns.add(getCustomBundlesLocation());
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
					logger.debug("Could not find Bundles at location '{}'.", locationPattern);
				}
			}
		} catch (final IOException e) {
			throw new RuntimeException("Error installing core Bundles: " + e.getMessage(), e);
		}
		return bundles;

	}

	/**
	 * Installs the Bundles in the repository.
	 * <p>
	 * This implementation uses RepositoryStoreService.
	 */
	protected List<Bundle> installRepositoryBundles() {
		final List<Bundle> bundles = new ArrayList<Bundle>();
		for (final FileInfo jarFile : getRepositoryStoreService().getBundleJarFiles()) {
			try {
				final String location = String.format("%s/%s", getRepositoryStoreService()
						.getBundleRepositoryLocation(), jarFile.getName());
				if (logger.isDebugEnabled()) {
					logger.debug("Installing Bundle: {}", location);
				}
				final ContentReader reader = getContentService().getReader(jarFile.getNodeRef(),
						ContentModel.PROP_CONTENT);
				final Bundle bundle = getFramework().getBundleContext().installBundle(location,
						reader.getContentInputStream());
				bundles.add(bundle);
			} catch (final Exception e) {
				logger.warn("Error installing Bundle: {}", e.getMessage(), e);
			}
		}
		return bundles;
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

	protected void startBundles(final List<Bundle> bundles) {
		final FrameworkWiring frameworkWiring = getFramework().adapt(FrameworkWiring.class);
		if (frameworkWiring.resolveBundles(bundles) == false) {
			if (logger.isWarnEnabled()) {
				logger.warn("Could not resolve all {} bundles.", bundles.size());
			}
		}
		for (final Bundle bundle : bundles) {
			if (isFragmentBundle(bundle) == false) {
				if (bundle.getState() == Bundle.RESOLVED) {
					startBundle(bundle);
				} else {
					if (logger.isWarnEnabled()) {
						logger.warn("Bundle '{}' ({}) is not resolved. Cannot start bundle.", bundle.getSymbolicName(),
								bundle.getBundleId());
					}
				}
			}
		}
	}

	protected void startBundle(final Bundle bundle) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Starting Bundle {}.", bundle.getBundleId());
			}
			bundle.start();
		} catch (final BundleException e) {
			if (logger.isErrorEnabled()) {
				logger.error("Error starting Bundle {}.", bundle.getBundleId(), e);
			}
		}
	}

	protected boolean isFragmentBundle(final Bundle bundle) {
		return bundle.getHeaders().get(Constants.FRAGMENT_HOST) != null;
	}

	/**
	 * 
	 * Unregisters services and {@link BundleListener}s and stops the {@link Framework}.
	 */
	public void destroy() {
		unregisterServices();
		stopFramework();
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
			getFramework().waitForStop(0);
		} catch (final BundleException e) {
			logger.error("Could not stop Framework.", e);
		} catch (final InterruptedException ignore) {}
	}

	/* Dependencies */

	@Required
	public void setFramework(final Framework framework) {
		Assert.notNull(framework, "Framework cannot be null.");
		this.framework = framework;
	}

	public Framework getFramework() {
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

	public void setRepositoryStoreService(final RepositoryStoreService repositoryFolderService) {
		Assert.notNull(repositoryFolderService);
		this.repositoryStoreService = repositoryFolderService;
	}

	protected RepositoryStoreService getRepositoryStoreService() {
		return repositoryStoreService;
	}

	public void setContentService(final ContentService contentService) {
		Assert.notNull(contentService);
		this.contentService = contentService;
	}

	protected ContentService getContentService() {
		return contentService;
	}

	/* Configuration */

	public void setConfiguration(final Configuration configuration) {
		Assert.notNull(configuration);
		this.configuration = configuration;
	}

	protected Configuration getConfiguration() {
		return configuration;
	}

	@Required
	public void setBlueprintBundlesLocation(final String blueprintBundleLocation) {
		Assert.hasText(blueprintBundleLocation);
		this.blueprintBundlesLocation = blueprintBundleLocation;
	}

	protected String getBlueprintBundlesLocation() {
		return blueprintBundlesLocation;
	}

	public void setStandardBundlesLocation(final String standardBundlesLocation) {
		this.standardBundlesLocation = standardBundlesLocation;
	}

	protected String getStandardBundlesLocation() {
		return standardBundlesLocation;
	}

	public void setCustomBundlesLocation(final String customBundlesLocation) {
		this.customBundlesLocation = customBundlesLocation;
	}

	protected String getCustomBundlesLocation() {
		return customBundlesLocation;
	}

	public boolean isRepositoryInstallEnabled() {
		return getConfiguration().getRepositoryBundlesEnabled();
	}

}
