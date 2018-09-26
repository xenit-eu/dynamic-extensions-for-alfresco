package com.github.dynamicextensionsalfresco.osgi.webscripts;

import org.springframework.extensions.webscripts.SearchPath;
import org.springframework.extensions.webscripts.Store;

/**
 * Defines operations for managing {@link Store}s in a {@link SearchPath}. This interface is intended to be exposed as
 * an OSGi service to let Bundles register and unregister {@link Store}s.
 * 
 * @author Laurens Fridael
 * 
 */
public interface SearchPathRegistry {

	void addStore(Store store);

	void removeStore(Store store);
}
