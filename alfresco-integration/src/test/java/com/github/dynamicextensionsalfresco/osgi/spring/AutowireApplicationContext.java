package nl.runnable.alfresco.osgi.spring;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * {@link ApplicationContext} that uses an {@link AutowireBeanFactory}.
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
		return new AutowireBeanFactory(getInternalParentBeanFactory());
	}
}
