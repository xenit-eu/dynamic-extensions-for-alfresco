package com.github.dynamicextensionsalfresco.osgi;

import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

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
	 * @param bundleContext
	 * @return The {@link ServiceRegistration}s.
	 */
	List<ServiceRegistration<?>> registerInBundleContext(BundleContext bundleContext);
}
