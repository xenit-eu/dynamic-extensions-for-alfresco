package com.github.dynamicextensionsalfresco.osgi;

import org.springframework.context.ApplicationContext;

/**
 * Holds constants for well-known names of beans in the OSGI child {@link ApplicationContext}.
 * 
 * @author Laurens Fridael
 * 
 */
class BeanNames {

	public static final String CONTAINER_FRAMEWORK = "osgi.container.Framework";

	public static final String CONTAINER_FRAMEWORK_MANAGER = "osgi.container.FrameworkManager";

	/**
	 * Constructor made private to prevent instantiation and subclassing.
	 */
	private BeanNames() {
	}
}
