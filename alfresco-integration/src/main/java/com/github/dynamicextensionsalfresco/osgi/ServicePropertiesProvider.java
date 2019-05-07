package com.github.dynamicextensionsalfresco.osgi;

import java.util.List;
import java.util.Map;

/**
 * Strategy for providing additional properties about an OSGi Service when it is registered by a
 * {@link BundleContextRegistrar}.
 * 
 * @author Laurens Fridael
 * 
 */
public interface ServicePropertiesProvider {

	/**
	 * Obtains properties for a given service.
	 * 
	 * @param service
	 *            The service.
	 * @param serviceNames
	 *            The names under which it will be registered.
	 * @return The service properties or null if none could be determined.
	 */
	Map<String, Object> getServiceProperties(Object service, List<String> serviceNames);

}
