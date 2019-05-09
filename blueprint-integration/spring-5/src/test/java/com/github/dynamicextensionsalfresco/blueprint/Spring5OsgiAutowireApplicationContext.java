package com.github.dynamicextensionsalfresco.blueprint;

import static org.mockito.Mockito.mock;

import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * {@link ApplicationContext} that uses an {@link Spring5OsgiAutowireBeanFactory}.
 * 
 * @author Laurens Fridael
 * 
 */
class Spring5OsgiAutowireApplicationContext extends ClassPathXmlApplicationContext {

	Spring5OsgiAutowireApplicationContext(final String location, final ApplicationContext parent) {
		super(new String[] { location }, Spring5OsgiAutowireApplicationContext.class, parent);
	}

	@Override
	protected DefaultListableBeanFactory createBeanFactory() {
		return new Spring5OsgiAutowireBeanFactory(getInternalParentBeanFactory(), mock(BundleContext.class));
	}
}
