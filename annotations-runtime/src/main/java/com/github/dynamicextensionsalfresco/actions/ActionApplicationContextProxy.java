package com.github.dynamicextensionsalfresco.actions;

import org.alfresco.repo.action.ActionServiceImpl;
import org.alfresco.repo.action.executer.ActionExecuter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * {@link ApplicationContext} pro   xy that intercepts calls to {@link org.springframework.context.ApplicationContext#getBean(String)} and consults an
 * {@link ActionExecuterRegistry} for matching {@link ActionExecuter}s. This proxy is added as a dependency to
 * {@link ActionServiceImpl} beans by {@link ActionServiceBeanPostProcessor}.
 *
 * @author Laurens Fridael
 * @author Laurent Van der Linden
 */
public class ActionApplicationContextProxy implements InvocationHandler, ApplicationContextAware {

	/* Dependencies */

	private ApplicationContext applicationContext;

	private ActionExecuterRegistry actionExecuterRegistry;

	/* Dependencies */

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public void setActionExecuterRegistry(final ActionExecuterRegistry actionExecuterRegistry) {
		this.actionExecuterRegistry = actionExecuterRegistry;
	}

	protected ActionExecuterRegistry getActionExecuterRegistry() {
		return actionExecuterRegistry;
	}


	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if ("getBean".equals(method.getName()) && args.length == 1 && args[0] instanceof String) {
			final String beanName = (String) args[0];
			if (applicationContext.containsBean(beanName)) {
				return applicationContext.getBean(beanName);
			} else {
				return getActionExecuterRegistry().getActionExecuter(beanName);
			}
		}
		return method.invoke(applicationContext, args);
	}
}
