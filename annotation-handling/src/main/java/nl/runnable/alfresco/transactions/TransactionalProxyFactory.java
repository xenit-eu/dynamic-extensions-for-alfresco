package nl.runnable.alfresco.transactions;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedHashSet;
import java.util.Set;

import nl.runnable.alfresco.transactions.annotations.Transactional;

import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.aopalliance.aop.Advice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxyFactory;
import org.springframework.aop.framework.DefaultAopProxyFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.MethodCallback;
import org.springframework.util.ReflectionUtils.MethodFilter;

/**
 * Factory for creating AOP proxies for beans with {@link Transactional}-annotated methods.
 * 
 * @author Laurens Fridael
 * 
 */
public class TransactionalProxyFactory {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private static MethodFilter TRANSACTIONAL_METHODS = new MethodFilter() {

		@Override
		public boolean matches(final Method method) {
			return AnnotationUtils.findAnnotation(method, Transactional.class) != null;
		}
	};

	/* Dependencies */

	private AopProxyFactory aopProxyFactory = new DefaultAopProxyFactory();

	private RetryingTransactionHelper retryingTransactionHelper;

	/* Main operations */

	/**
	 * Indicates if the given target object has any {@link Transactional}-annotated methods.
	 * 
	 * @param target
	 * @return
	 */
	public boolean hasTransactionalMethods(final Object target) {
		Assert.notNull(target);
		return (findTransactionalMethods(target).isEmpty() == false);
	}

	/**
	 * Creates an AOP proxy for the given target with advice for handling {@link Transactional} methods.
	 * 
	 * @param target
	 * @return The AOP proxy.
	 * @throws IllegalStateException
	 *             If the target does not have any {@link Transactional} methods. Use
	 *             {@link #hasTransactionalMethods(Object)} to test for this.
	 */
	@SuppressWarnings("unchecked")
	public <T> T createTransactionalProxy(final T target) {
		final AdvisedSupport config = new AdvisedSupport();
		config.addAdvice(createTransactionalAdvice(target));
		config.setTarget(target);
		return ((T) getAopProxyFactory().createAopProxy(config).getProxy());
	}

	/* Utility operations */

	protected Advice createTransactionalAdvice(final Object target) {
		final Set<Method> methods = findTransactionalMethods(target);
		Assert.state(methods.isEmpty() == false, "Could not find @Transactional-annotated methods on target.");
		return new TransactionalAdvice(getRetryingTransactionHelper(), methods);
	}

	/**
	 * Obtains the {@link Method}s annotated with {@link Transactional} for the given bean.
	 * 
	 * @param bean
	 * @return
	 */
	protected Set<Method> findTransactionalMethods(final Object bean) {
		final Set<Method> transactionalMethods = new LinkedHashSet<Method>();
		ReflectionUtils.doWithMethods(bean.getClass(), new MethodCallback() {

			@Override
			public void doWith(final Method method) throws IllegalArgumentException, IllegalAccessException {
				if ((method.getModifiers() & Modifier.PUBLIC) > 0) {
					transactionalMethods.add(method);
				} else {
					logger.warn(String.format("Found @Transactional annotation on non-public method '%s'. "
							+ "@Transactional annotations can only be used on public methods.",
							ClassUtils.getQualifiedMethodName(method)));
				}
			}
		}, TRANSACTIONAL_METHODS);
		return transactionalMethods;
	}

	/* Dependencies */

	public void setAopProxyFactory(final AopProxyFactory aopProxyFactory) {
		Assert.notNull(aopProxyFactory);
		this.aopProxyFactory = aopProxyFactory;
	}

	protected AopProxyFactory getAopProxyFactory() {
		return aopProxyFactory;
	}

	public void setRetryingTransactionHelper(final RetryingTransactionHelper retryingTransactionHelper) {
		Assert.notNull(retryingTransactionHelper);
		this.retryingTransactionHelper = retryingTransactionHelper;
	}

	protected RetryingTransactionHelper getRetryingTransactionHelper() {
		return retryingTransactionHelper;
	}

}
