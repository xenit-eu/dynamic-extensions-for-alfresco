package nl.runnable.alfresco.aop;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.Assert;

/**
 * {@link BeanPostProcessor} that creates AOP proxies using {@link AdvisedProxyFactory}.
 * 
 * @author Laurens Fridael
 * 
 */
public class AdvisedProxyBeanPostProcessor implements BeanPostProcessor {

	/* Dependencies */

	private AdvisedProxyFactory advisedProxyFactory;

	/* Main operations */

	@Override
	public Object postProcessBeforeInitialization(final Object bean, final String name) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, final String name) throws BeansException {
		if (advisedProxyFactory.hasMethodWithAnySupportedAnnotation(bean)) {
			bean = advisedProxyFactory.createAdvisedProxy(bean);
		}
		return bean;
	}

	/* Dependencies */

	public void setAdvisedProxyFactory(final AdvisedProxyFactory advisedProxyFactory) {
		Assert.notNull(advisedProxyFactory);
		this.advisedProxyFactory = advisedProxyFactory;
	}

}
