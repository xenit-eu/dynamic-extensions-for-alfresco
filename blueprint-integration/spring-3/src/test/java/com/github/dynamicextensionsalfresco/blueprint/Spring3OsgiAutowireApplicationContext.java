package com.github.dynamicextensionsalfresco.blueprint;

import static org.mockito.Mockito.mock;

import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * {@link ApplicationContext} that uses an {@link Spring3OsgiAutowireBeanFactory}.
 * 
 * @author Laurens Fridael
 * 
 */
class Spring3OsgiAutowireApplicationContext extends ClassPathXmlApplicationContext {

	Spring3OsgiAutowireApplicationContext(final String location, final ApplicationContext parent) {
		super(new String[] { location }, Spring3OsgiAutowireApplicationContext.class, parent);
	}

	@Override
	protected DefaultListableBeanFactory createBeanFactory() {
		return new Spring3OsgiAutowireBeanFactory(getInternalParentBeanFactory(), mock(BundleContext.class));
	}
}
