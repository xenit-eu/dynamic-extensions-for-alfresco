package com.github.dynamicextensionsalfresco.osgi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;

public class HostApplicationServicePropertiesProvider implements ServicePropertiesProvider {

	private static final String HOST_APPLICATION_PROPERTY_NAME = "hostApplication";

	private String hostApplication;

	private Map<String, Object> properties;

	@Required
	public void setHostApplication(final String applicationName) {
		this.hostApplication = applicationName;
	}

	protected String getHostApplication() {
		return hostApplication;
	}

	protected Map<String, Object> getProperties() {
		if (properties == null) {
			properties = new HashMap<String, Object>();
			properties.put(HOST_APPLICATION_PROPERTY_NAME, getHostApplication());
		}
		return properties;
	}

	@Override
	public Map<String, Object> getServiceProperties(final Object service, final List<String> serviceNames) {
		return getProperties();
	}

}
