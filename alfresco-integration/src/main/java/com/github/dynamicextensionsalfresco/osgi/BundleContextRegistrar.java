package com.github.dynamicextensionsalfresco.osgi;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.util.List;

/**
 * Strategy for registering services in a {@link BundleContext}.
 * 
 * @author Laurens Fridael
 * 
 */
public interface BundleContextRegistrar {

	/**
	 * Registers services in a {@link BundleContext} and returns the corresponding {@link ServiceRegistration}s.
	 * 
	 * @param bundleContext The bundle context
	 * @return The {@link ServiceRegistration}s.
	 */
	List<ServiceRegistration<?>> registerInBundleContext(BundleContext bundleContext);
}
