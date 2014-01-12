package com.github.dynamicextensionsalfresco.osgi.felix;

import com.github.dynamicextensionsalfresco.osgi.spring.AbstractFrameworkFactoryBean;

import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;
import org.springframework.beans.factory.FactoryBean;

/**
 * {@link FactoryBean} that creates Felix-based {@link Framework}s.
 * 
 * @author Laurens Fridael
 * 
 */
public class FelixFrameworkFactoryBean extends AbstractFrameworkFactoryBean {

	private FrameworkFactory frameworkFactory;

	@Override
	protected FrameworkFactory getFrameworkFactory() {
		if (frameworkFactory == null) {
			frameworkFactory = new org.apache.felix.framework.FrameworkFactory();
		}
		return frameworkFactory;
	}

}
