package com.github.dynamicextensionsalfresco.osgi;

import java.util.Collection;
import java.util.Iterator;

import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

/**
 * Holds configuration values, such as {@link SystemPackage}s and {@link ServiceDefinition}s.
 * <p>
 * Using a separate class, instead of a generic {@link Collection}, works around issues when autowiring dependencies in
 * an {@link ApplicationContext} that contains other {@link Collection} top-level beans.
 * 
 * @author Laurens Fridael
 * 
 * @param <T>
 */
public class ConfigurationValues<T> implements Iterable<T> {

	private final Collection<T> values;

	public ConfigurationValues(final Collection<T> values) {
		Assert.notNull(values);
		this.values = values;
	}

	public Collection<T> getValues() {
		return values;
	}

	@Override
	public Iterator<T> iterator() {
		return values.iterator();
	}

}
