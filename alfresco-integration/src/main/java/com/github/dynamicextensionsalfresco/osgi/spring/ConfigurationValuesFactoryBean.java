package com.github.dynamicextensionsalfresco.osgi.spring;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.github.dynamicextensionsalfresco.osgi.ConfigurationValues;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.Assert;

/**
 * {@link FactoryBean} that combines all the items from multiple Sets into a single Set.
 * <p>
 * This implementation preserves the original item order.
 * 
 * @author Laurens Fridael
 * 
 * @param <T>
 */
public class ConfigurationValuesFactoryBean<T> implements FactoryBean<ConfigurationValues<T>> {

	/* Configuration */

	private List<Set<T>> sets;

	private ConfigurationValues<T> instance;

	/* Main operations */

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Class getObjectType() {
		return ConfigurationValues.class;
	}

	@Override
	public ConfigurationValues<T> getObject() throws Exception {
		if (instance == null) {
			instance = createContainerConfiguration();
		}
		return instance;
	}

	/* Utility operations */

	protected ConfigurationValues<T> createContainerConfiguration() {
		final Set<T> items = new LinkedHashSet<T>(getTotalSize());
		for (final Set<T> set : getSets()) {
			items.addAll(set);
		}
		return new ConfigurationValues<T>(items);
	}

	protected int getTotalSize() {
		int totalSize = 0;
		for (final Set<T> set : getSets()) {
			totalSize += set.size();
		}
		return totalSize;
	}

	/* Configuration */

	public void setSets(final List<Set<T>> sets) {
		Assert.notNull(sets);
		this.sets = sets;
	}

	protected List<Set<T>> getSets() {
		return sets;
	}
}
