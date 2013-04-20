package nl.runnable.alfresco.osgi;

import java.util.Collection;
import java.util.Collections;

import nl.runnable.alfresco.webscripts.integration.RegistryProvider;

import org.alfresco.repo.module.AbstractModuleComponent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.extensions.webscripts.Container;
import org.springframework.extensions.webscripts.Registry;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

/**
 * Module component that manages the OSGI child {@link ApplicationContext} and initializes the {@link FrameworkManager}
 * within it.
 * 
 * @author Laurens Fridael
 * 
 */
public class OsgiContainerModuleComponent extends AbstractModuleComponent implements ApplicationContextAware,
		FrameworkService, RegistryProvider {

	/* Dependencies */

	/**
	 * The containing {@link ApplicationContext}.
	 */
	private ConfigurableWebApplicationContext applicationContext;

	/**
	 * The {@link Container} must be reset manually after restarting the OSGi framework.
	 */
	private Container webScriptsContainer;

	/* Configuration */

	private String[] applicationContextConfigLocations;

	/* State */

	/**
	 * The child {@link ApplicationContext} containing the OSGi framework.
	 * <p>
	 * This {@link ApplicationContext} can be destroyed and recreated, effectively enabling clients to restart the OSGi
	 * container.
	 */
	private ConfigurableWebApplicationContext childApplicationContext;

	/* Main module operations */

	/**
	 * Initializes this module.
	 */
	@Override
	protected void executeInternal() {
		startFramework();
	}

	/**
	 * Called on bean destroy.
	 */
	public void destroy() {
		stopFramework();
	}

	/* Main FrameworkService operations */

	@Override
	public void restartFramework() {
		try {
			stopFramework();
		} finally {
			startFramework();
			getWebScriptsContainer().reset();
		}
	}

	/* Main RegistryProvider operations */

	@Override
	public Collection<Registry> getRegistries() {
		if (childApplicationContext != null) {
			return childApplicationContext.getBeansOfType(Registry.class).values();
		} else {
			return Collections.emptySet();
		}
	}

	/* Utility operations */

	protected void startFramework() {
		if (childApplicationContext == null) {
			try {
				initializeOsgiContainerApplicationContext();
			} catch (final Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	private void initializeOsgiContainerApplicationContext() {
		childApplicationContext = new XmlWebApplicationContext();
		childApplicationContext.setParent(getApplicationContext());
		childApplicationContext.setServletContext(getApplicationContext().getServletContext());
		childApplicationContext.setConfigLocation(StringUtils.arrayToDelimitedString(
				getApplicationContextConfigLocations(), ","));
		childApplicationContext.refresh();
	}

	protected void stopFramework() {
		if (childApplicationContext != null) {
			try {
				childApplicationContext.close();
			} finally {
				childApplicationContext = null;
			}
		}
	}

	/* Dependencies */

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) {
		this.applicationContext = (ConfigurableWebApplicationContext) applicationContext;
	}

	protected ConfigurableWebApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public void setWebScriptsContainer(final Container webScriptsContainer) {
		this.webScriptsContainer = webScriptsContainer;
	}

	public Container getWebScriptsContainer() {
		return webScriptsContainer;
	}

	/* Configuration */

	public void setApplicationContextConfigLocations(final String[] applicationContextConfigLocations) {
		this.applicationContextConfigLocations = applicationContextConfigLocations;
	}

	protected String[] getApplicationContextConfigLocations() {
		return applicationContextConfigLocations;
	}

	/* State */

	/**
	 * Obtains the {@link FrameworkManager} from the OSGi container{@link ApplicationContext}.
	 */
	protected FrameworkManager getFrameworkManager() {
		Assert.state(childApplicationContext != null);
		return childApplicationContext.getBean(BeanNames.CONTAINER_FRAMEWORK_MANAGER, FrameworkManager.class);
	}

}
