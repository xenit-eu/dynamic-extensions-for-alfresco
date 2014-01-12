package com.github.dynamicextensionsalfresco.osgi.spring;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;

/**
 * Strategy for resolving autowire candidates.
 * 
 * @author Laurens Fridael
 * 
 */
public interface DependencyResolver {

	/**
	 * Resolves a dependency
	 * 
	 * @param beanFactory
	 * @param beanName
	 * @param type
	 * @param dependencyDescriptor
	 */
	void resolveDependency(BeanFactory beanFactory, String beanName, Class<?> type,
			DependencyDescriptor dependencyDescriptor);
}
