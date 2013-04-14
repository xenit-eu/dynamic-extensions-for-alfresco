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
