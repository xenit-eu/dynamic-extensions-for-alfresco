package com.github.dynamicextensionsalfresco.osgi;

import java.util.Arrays;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

/**
 * Represents configuration for registering a bean from an {@link ApplicationContext} as a service in a
 * {@link BundleContext}.
 * 
 * @author Laurens Fridael
 * 
 */
public class ServiceDefinition {
	private final List<String> beanNames;

	private final List<String> serviceNames;

	private String serviceType;

	private String platformVersion;

	public ServiceDefinition(final String[] beanNames, final String[] serviceNames, final String serviceType,
			final String platformVersion) {
		Assert.notEmpty(beanNames, "Bean names cannot be empty.");
		Assert.notEmpty(serviceNames, "Service names cannot be empty.");

		this.beanNames = Arrays.asList(beanNames);
		this.serviceNames = Arrays.asList(serviceNames);
		this.serviceType = serviceType;
		this.platformVersion = platformVersion;
	}

	public ServiceDefinition(final String[] beanNames, final String[] serviceNames, final String serviceType) {
		Assert.notEmpty(beanNames, "Bean names cannot be empty.");
		Assert.notEmpty(serviceNames, "Service names cannot be empty.");

		this.beanNames = Arrays.asList(beanNames);
		this.serviceNames = Arrays.asList(serviceNames);
		this.serviceType = serviceType;
	}

	public ServiceDefinition(final String beanName, final String... serviceNames) {
		Assert.hasText(beanName, "Bean names cannot be empty.");
		Assert.notEmpty(serviceNames, "Service names cannot be empty.");

		this.beanNames = Arrays.asList(beanName);
		this.serviceNames = Arrays.asList(serviceNames);
	}

	public ServiceDefinition(final String[] beanNames, final String... serviceNames) {
		Assert.notEmpty(beanNames, "Bean names cannot be empty.");
		Assert.notEmpty(serviceNames, "Service names cannot be empty.");
		this.beanNames = Arrays.asList(beanNames);
		this.serviceNames = Arrays.asList(serviceNames);
	}

	public List<String> getBeanNames() {
		return beanNames;
	}

	public List<String> getServiceNames() {
		return serviceNames;
	}

	public String getServiceType() {
		return serviceType;
	}

	public String getPlatformVersion() {
		return platformVersion;
	}

}
