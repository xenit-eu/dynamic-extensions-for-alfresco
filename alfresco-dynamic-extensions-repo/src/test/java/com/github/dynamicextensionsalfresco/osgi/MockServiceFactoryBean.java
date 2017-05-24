package com.github.dynamicextensionsalfresco.osgi;

import static org.mockito.Mockito.*;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

/**
 * FactoryBean that creates a mock for a given class.
 * 
 * @author Laurens Fridael
 * 
 */
public class MockServiceFactoryBean implements FactoryBean<Object> {

	private Class<?> clazz;

	private Object service;

	@Required
	public void setClass(final Class<?> clazz) {
		Assert.notNull(clazz);
		this.clazz = clazz;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public Class<?> getObjectType() {
		return clazz;
	}

	@Override
	public Object getObject() throws Exception {
		if (service == null) {
			service = mock(clazz);
		}
		return service;
	}
}
