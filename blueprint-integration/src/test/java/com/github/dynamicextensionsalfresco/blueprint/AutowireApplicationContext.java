package com.github.dynamicextensionsalfresco.blueprint;

import static org.mockito.Mockito.mock;

import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * {@link ApplicationContext} that uses an {@link OsgiAutowireBeanFactory}.
 * 
 * @author Laurens Fridael
 * 
 */
class AutowireApplicationContext extends ClassPathXmlApplicationContext {

	AutowireApplicationContext(final String location, final ApplicationContext parent) {
		super(new String[] { location }, AutowireApplicationContext.class, parent);
	}

	@Override
	protected DefaultListableBeanFactory createBeanFactory() {
		return new OsgiAutowireBeanFactory(getInternalParentBeanFactory(), mock(BundleContext.class));
	}
}
