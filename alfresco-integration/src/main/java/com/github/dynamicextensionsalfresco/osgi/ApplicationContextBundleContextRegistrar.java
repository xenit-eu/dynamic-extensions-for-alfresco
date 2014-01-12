package com.github.dynamicextensionsalfresco.osgi;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.launch.Framework;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Registers the {@link ApplicationContext} as a service in the OSGi {@link Framework}.
 * <p>
 * This enables OSGi {@link Bundle}s to directly reference named dependencies.
 * 
 * @author Laurens Fridael
 * 
 */
public class ApplicationContextBundleContextRegistrar implements BundleContextRegistrar, ApplicationContextAware {
	private static final String COMPONENT_NAME = "HostApplicationContext";

	private static final String OSGI_SERVICE_BLUEPRINT_COMPNAME = "osgi.service.blueprint.compname";

	/* Dependencies */

	private ApplicationContext applicationContext;

	/* Main operations */

	@Override
	public List<ServiceRegistration<?>> registerInBundleContext(final BundleContext bundleContext) {
		final Hashtable<String, Object> serviceProperties = new Hashtable<String, Object>();
		serviceProperties.put(OSGI_SERVICE_BLUEPRINT_COMPNAME, COMPONENT_NAME);
		serviceProperties.put("hostApplication", "alfresco");
		final List<ServiceRegistration<?>> registrations = new ArrayList<ServiceRegistration<?>>();
		registrations.add(bundleContext
				.registerService(ApplicationContext.class, applicationContext, serviceProperties));
		return registrations;
	}

	/* Dependencies */

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
