package com.github.dynamicextensionsalfresco.osgi.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Spring {@link FactoryBean} for creating a child {@link ApplicationContext}.
 * <p>
 * This class can operate in createSingletons or prototype mode.
 * 
 * @author Laurens Fridael
 * 
 */
public class ChildApplicationContextFactoryBean implements FactoryBean<ClassPathXmlApplicationContext>,
		ApplicationContextAware {

	/* Dependencies */

	private ApplicationContext parentApplicationContext;

	/* Configuration */

	private String[] configLocations;

	private boolean createSingletons = true;

	/* State */

	private ClassPathXmlApplicationContext childApplicationContext;

	/* Main operations */

	@Override
	public boolean isSingleton() {
		return createSingletons;
	}

	@Override
	public Class<? extends ClassPathXmlApplicationContext> getObjectType() {
		return ClassPathXmlApplicationContext.class;
	}

	@Override
	public ClassPathXmlApplicationContext getObject() {
		if (isCreateSingletons()) {
			if (childApplicationContext == null) {
				childApplicationContext = createOsgiContainerApplicationContext();
			}
			return childApplicationContext;
		} else {
			return createOsgiContainerApplicationContext();
		}
	}

	public void destroy() {
		closeOsgiContainerApplicationContext();
	}

	/* Dependencies */

	protected ClassPathXmlApplicationContext createOsgiContainerApplicationContext() {
		return new ClassPathXmlApplicationContext(getConfigLocations(), getParentApplicationContext());
	}

	protected void closeOsgiContainerApplicationContext() {
		if (childApplicationContext != null) {
			childApplicationContext.close();
		}
	}

	/* Dependencies */

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
		this.parentApplicationContext = applicationContext;
	}

	protected ApplicationContext getParentApplicationContext() {
		return parentApplicationContext;
	}

	/* Configuration */

	public void setConfigLocations(final String[] configLocations) {
		this.configLocations = configLocations;
	}

	protected String[] getConfigLocations() {
		return configLocations;
	}

	public void setCreateSingletons(final boolean createSingletons) {
		this.createSingletons = createSingletons;
	}

	public boolean isCreateSingletons() {
		return createSingletons;
	}

}
