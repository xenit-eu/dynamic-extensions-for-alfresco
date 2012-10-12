package nl.runnable.alfresco.actions;

import org.alfresco.repo.action.ActionServiceImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;

/**
 * {@link BeanPostProcessor} that customizes {@link ActionServiceImpl} beans by substituting the
 * {@link ApplicationContext} dependency with an {@link ActionApplicationContextProxy}. This proxy holds a reference to
 * the original {@link ApplicationContext} dependency.
 * 
 * @author Laurens Fridael
 * 
 */
public class ActionServiceBeanPostProcessor implements BeanPostProcessor {

	/* Configuration */

	private String actionServiceBeanName;

	private ActionApplicationContextProxy actionApplicationContextProxy;

	/* Operations */

	@Override
	public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
		if (beanName.equals(getActionServiceBeanName()) && bean instanceof ActionServiceImpl) {
			((ActionServiceImpl) bean).setApplicationContext(getActionApplicationContextProxy());
		}
		return bean;
	}

	/* Configuration */

	public void setActionServiceBeanName(final String beanName) {
		this.actionServiceBeanName = beanName;
	}

	protected String getActionServiceBeanName() {
		return actionServiceBeanName;
	}

	public void setActionApplicationContextProxy(final ActionApplicationContextProxy actionApplicationContextProxy) {
		this.actionApplicationContextProxy = actionApplicationContextProxy;
	}

	public ActionApplicationContextProxy getActionApplicationContextProxy() {
		return actionApplicationContextProxy;
	}

}
