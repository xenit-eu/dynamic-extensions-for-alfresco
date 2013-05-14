package nl.runnable.alfresco.blueprint;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.alfresco.service.transaction.TransactionService;
import org.eclipse.gemini.blueprint.context.DelegatedExecutionOsgiBundleApplicationContext;
import org.eclipse.gemini.blueprint.context.OsgiBundleApplicationContextExecutor;
import org.eclipse.gemini.blueprint.context.support.ContextClassLoaderProvider;
import org.eclipse.gemini.blueprint.context.support.DefaultContextClassLoaderProvider;
import org.eclipse.gemini.blueprint.extender.OsgiApplicationContextCreator;
import org.eclipse.gemini.blueprint.extender.internal.dependencies.startup.DependencyWaiterApplicationContextExecutor;
import org.eclipse.gemini.blueprint.extender.support.ApplicationContextConfiguration;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

/**
 * {@link OsgiApplicationContextCreator} that creates a {@link DynamicExtensionsApplicationContext} for each
 * {@link Bundle}.
 * <p>
 * <strong>New in milestone 4</strong>: the OSGI bundle MUST contain the header
 * <code>Alfresco-Dynamic-Extension: true</code> for it to be considered a Dynamic Extension. Versions prior to
 * milestone 4 did not have this requirement.
 * <p>
 * This implementation automatically uninstalls Dynamic Extension bundles with a duplicate symbolic name, even if they
 * are of a different version. While OSGi allows running parallel versions of a given bundle, this additional constraint
 * prevents duplicate Web Script, Behaviour and Action bindings in the repository.
 * <p>
 * Installing parallel versions of regular OSGi bundles - that is: bundles without the
 * <code>Alfresco-Dynamic-Extension</code> header - is still allowed.
 * 
 * @author Laurens Fridael
 * 
 */
public class DynamicExtensionsApplicationContextCreator implements OsgiApplicationContextCreator {

	private static final String ALFRESCO_DYNAMIC_EXTENSION_HEADER = "Alfresco-Dynamic-Extension";

	private static final String PARENT_APPLICATION_CONTEXT_HEADER = "Parent-Application-Context";

	private static final String HOST_PARENT_APPLICATION_CONTEXT = "host";

	private static final String HOST_APPLICATION_CONTEXT_BEAN_NAME = "HostApplicationContext";

	private static final String OSGI_SERVICE_BLUEPRINT_COMPNAME = "osgi.service.blueprint.compname";

	private final Logger logger = LoggerFactory.getLogger(getClass());

	/* Configuration */

	private String modelLocationPattern;

	/* Operations */

	@Override
	public DelegatedExecutionOsgiBundleApplicationContext createApplicationContext(final BundleContext bundleContext)
			throws Exception {
		final Bundle bundle = bundleContext.getBundle();
		if (isAlfrescoDynamicExtension(bundle) == false) {
			return null;
		}
		uninstallBundlesWithDuplicateSymbolicName(bundleContext);
		/*
		 * WARNING: Avoid creating an instance of
		 * org.eclipse.gemini.blueprint.extender.internal.blueprint.activator.support.BlueprintContainerConfig, since
		 * this package is not exported by the extender Bundle. You should use
		 * org.eclipse.gemini.blueprint.extender.support.ApplicationContextConfiguration instead.
		 * 
		 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=379384
		 * 
		 * UPDATE: This issue still seems to occur with Blueprint 1.0.2.RELEASE. Since Dynamic Extensions de-emphasizes
		 * the direct use of OSGi anyway, Blueprint configuration will definitely not be supported.
		 */
		final ApplicationContextConfiguration config = new ApplicationContextConfiguration(bundle);
		String[] configurationLocations = null;
		if (config.isSpringPoweredBundle()) {
			configurationLocations = config.getConfigurationLocations();
		}
		if (logger.isDebugEnabled()) {
			if (configurationLocations != null && configurationLocations.length > 0) {
				logger.debug("Initializing Dynamic Extension '{}' using Spring configuration: {}",
						bundle.getSymbolicName(), Arrays.asList(configurationLocations));
			} else {
				logger.debug("Initializing Dynamic Extension '{}'.", bundle.getSymbolicName());
			}
		}
		ApplicationContext parentApplicationContext = null;
		if (isUseParentApplicationContext(bundle)) {
			parentApplicationContext = getHostApplicationContext(bundleContext);
		}
		final DynamicExtensionsApplicationContext applicationContext = new DynamicExtensionsApplicationContext(
				configurationLocations, parentApplicationContext);
		applicationContext.setBundleContext(bundleContext);

		final DefaultContextClassLoaderProvider contextClassLoaderProvider = new DefaultContextClassLoaderProvider();
		contextClassLoaderProvider.setBeanClassLoader(ApplicationContext.class.getClassLoader());
		applicationContext.setContextClassLoaderProvider(contextClassLoaderProvider);

		applicationContext.setPublishContextAsService(config.isPublishContextAsService());
		if (StringUtils.hasText(getModelLocationPattern())) {
			applicationContext.setModelLocationPattern(getModelLocationPattern());
		}
		return applicationContext;

	}

	/* Utility operations */

	protected boolean isAlfrescoDynamicExtension(final Bundle bundle) {
		return Boolean.valueOf(bundle.getHeaders().get(ALFRESCO_DYNAMIC_EXTENSION_HEADER));
	}

	/**
	 * Uninstalls {@link Bundle}s with symbolic names equal to that of the {@link Bundle} represented by the given the
	 * {@link BundleContext}
	 * 
	 * @param bundleContext
	 * @throws BundleException
	 */
	protected void uninstallBundlesWithDuplicateSymbolicName(final BundleContext bundleContext) throws BundleException {
		final Bundle currentBundle = bundleContext.getBundle();
		final Set<Bundle> duplicateInstalledBundles = new LinkedHashSet<Bundle>();
		for (final Bundle installedBundle : bundleContext.getBundles()) {
			if (currentBundle != installedBundle && currentBundle.getBundleId() != installedBundle.getBundleId()
					&& currentBundle.getSymbolicName().equals(installedBundle.getSymbolicName())) {
				duplicateInstalledBundles.add(installedBundle);
			}
		}
		for (final Bundle installedBundle : duplicateInstalledBundles) {
			try {
				if (logger.isWarnEnabled()) {
					logger.warn("Overwriting existing installation of Dynamic Extension '{}'. "
							+ "It is recommended not to deploy multiple versions of the same Dynamic Extension.",
							installedBundle.getSymbolicName());
				}
				installedBundle.uninstall();
			} catch (final BundleException e) {
				logger.error("Error uninstalling Bundle: {}", e.getMessage(), e);
			}
		}
	}

	protected boolean isUseParentApplicationContext(final Bundle bundle) {
		final String header = bundle.getHeaders().get(PARENT_APPLICATION_CONTEXT_HEADER);
		return (header == null || header.equals(HOST_PARENT_APPLICATION_CONTEXT));
	}

	protected ApplicationContext getHostApplicationContext(final BundleContext bundleContext) {
		final ServiceReference<?> serviceReference = getServiceReferenceWithBeanName(bundleContext,
				ApplicationContext.class.getName(), HOST_APPLICATION_CONTEXT_BEAN_NAME);
		if (serviceReference != null) {
			return new HostApplicationContext((ApplicationContext) bundleContext.getService(serviceReference));
		} else {
			return null;
		}
	}

	protected ServiceReference<?> getServiceReferenceWithBeanName(final BundleContext bundleContext,
			final String serviceName, final String beanName) {
		try {
			final String filter = String.format("(%s=%s)", OSGI_SERVICE_BLUEPRINT_COMPNAME, beanName);
			final ServiceReference<?>[] serviceReferences = bundleContext.getServiceReferences(serviceName, filter);
			if (serviceReferences != null && serviceReferences.length > 0) {
				return serviceReferences[0];
			} else {
				return null;
			}
		} catch (final InvalidSyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	/* Configuration */

	public void setModelLocationPattern(final String modelLocationPattern) {
		this.modelLocationPattern = modelLocationPattern;
	}

	protected String getModelLocationPattern() {
		return modelLocationPattern;
	}
}
