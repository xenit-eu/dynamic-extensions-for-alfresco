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

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
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

	private Configuration configuration;

	private RepositoryStoreService repositoryStoreService;

	private ContentService contentService;

	/* Configuration */

	private List<BundleListener> bundleListeners = Collections.emptyList();

	private List<ServiceListener> serviceListeners = Collections.emptyList();

	private String blueprintBundlesLocation;

	private String fileInstallBundlesLocation;

	private String standardBundlesLocation;

	private boolean fileInstallEnabled = true;

	private boolean repositoryInstallEnabled = true;

	/**
	 * Starts the {@link Framework} and registers services and {@link BundleListener}s.
	 * 
	 * @throws BundleException
	 */
	public void initialize() {
		startFramework();
		registerFramework();
		final List<Bundle> coreBundles = installCoreBundles();
		registerServices();
		startBundles(coreBundles);
		if (isFileInstallEnabled() == false) {
			// Start the bundles manually.
			// TODO: Determine if this is the correct way to handle this.
			startBundles(installFilesystemBundles());
		}
		if (isRepositoryInstallEnabled()) {
			startBundles(installRepositoryBundles());
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
					logger.debug("Could not find Bundles at location '{}'.", locationPattern);
				}
			}
		} catch (final IOException e) {
			throw new RuntimeException("Error installing core Bundles: " + e.getMessage(), e);
		}
		return bundles;

	}

	/**
	 * Installs the Bundles on the filesystem.
	 * <p>
	 * This implementation looks for JAR files in the directories configured for File Install.
	 * 
	 * @return
	 */
	protected List<Bundle> installFilesystemBundles() {
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
				} catch (final Exception e) {
					logger.warn("Error installing Bundle: {}", e.getMessage(), e);
				}
			}
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

	/**
	 * Registers the {@link Framework} with the {@link ExtensionRegistry}.
	 */
	protected void registerFramework() {
		getConfiguration().setFileInstallPaths(fileInstallConfigurer.getDirectoriesAsAbsolutePaths());
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
			getFramework().waitForStop(0);
		} catch (final BundleException e) {
			logger.error("Could not stop Framework.", e);
		} catch (final InterruptedException e) {
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

	public void setConfiguration(final Configuration configuration) {
		Assert.notNull(configuration);
		this.configuration = configuration;
	}

	protected Configuration getConfiguration() {
		return configuration;
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

	public void setRepositoryInstallEnabled(final boolean repositoryInstallEnabled) {
		this.repositoryInstallEnabled = repositoryInstallEnabled;
	}

	public boolean isRepositoryInstallEnabled() {
		return repositoryInstallEnabled;
	}

}
