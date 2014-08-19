package com.github.dynamicextensionsalfresco.proxy;

import org.osgi.framework.Filter;

/**
 * Model for the service filter to allow updates at runtime.
 *
 * @author Laurent Van der Linden
 */
public interface FilterModel {
	Filter getServiceFilter();
	void setServiceFilter(Filter serviceFilter);
	void setServiceFilterString(String ldapFilter);
}
