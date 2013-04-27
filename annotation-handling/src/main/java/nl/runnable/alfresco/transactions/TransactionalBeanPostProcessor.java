package nl.runnable.alfresco.transactions;

import nl.runnable.alfresco.transactions.annotations.Transactional;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.Assert;

/**
 * {@link BeanPostProcessor} that creates AOP proxies for beans with {@link Transactional}-annotated methods.
 * 
 * @author Laurens Fridael
 * 
 */
public class TransactionalBeanPostProcessor implements BeanPostProcessor {

	/* Dependencies */

	private TransactionalProxyFactory transactionalProxyFactory;

	/* Main operations */

	@Override
	public Object postProcessBeforeInitialization(final Object bean, final String name) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, final String name) throws BeansException {
		if (transactionalProxyFactory.hasTransactionalMethods(bean)) {
			bean = transactionalProxyFactory.createTransactionalProxy(bean);
		}
		return bean;
	}

	/* Dependencies */

	public void setTransactionalProxyFactory(final TransactionalProxyFactory transactionalProxyFactory) {
		Assert.notNull(transactionalProxyFactory);
		this.transactionalProxyFactory = transactionalProxyFactory;
	}

}
