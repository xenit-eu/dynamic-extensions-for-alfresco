package com.github.dynamicextensionsalfresco.osgi.spring;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.Assert;

/**
 * Abstract base class for {@link FactoryBean}s that use configuration files.
 * 
 * @author Laurens Fridael
 * 
 * @param <T>
 */
public abstract class AbstractConfigurationFileFactoryBean<T> implements FactoryBean<T>, ResourceLoaderAware {

	/* Dependencies */

	private ResourcePatternResolver resourcePatternResolver;

	/* Configuration */

	private List<Resource> configurations = Collections.emptyList();

	private List<String> configurationLocations = Collections.emptyList();

	/* Dependencies */

	@Override
	public void setResourceLoader(final ResourceLoader resourceLoader) {
		Assert.isInstanceOf(ResourcePatternResolver.class, resourceLoader);
		this.resourcePatternResolver = (ResourcePatternResolver) resourceLoader;
	}

	protected ResourcePatternResolver getResourcePatternResolver() {
		return resourcePatternResolver;
	}

	/* Utility operations */

	protected List<Resource> resolveConfigurations() throws IOException {
		final List<Resource> configurations = new ArrayList<Resource>(this.configurations);
		for (final String location : getConfigurationLocations()) {
			configurations.addAll(Arrays.asList(getResourcePatternResolver().getResources(location)));
		}
		return configurations;
	}

	/* Configuration */

	public void setConfigurations(final List<Resource> configurations) {
		Assert.notNull(configurations);
		this.configurations = configurations;
	}

	protected List<Resource> getConfigurations() {
		return configurations;
	}

	public void setConfigurationLocations(final List<String> configurationLocations) {
		this.configurationLocations = configurationLocations;
	}

	protected List<String> getConfigurationLocations() {
		return configurationLocations;
	}
}
