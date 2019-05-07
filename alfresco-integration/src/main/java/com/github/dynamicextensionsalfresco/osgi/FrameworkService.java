package com.github.dynamicextensionsalfresco.osgi;

/**
 * Defines operations for managing the Dynamic Extensions framework.
 * 
 * @author Laurens Fridael
 * 
 */
public interface FrameworkService {

	void restartFramework();

	FrameworkManager getFrameworkManager();
}
