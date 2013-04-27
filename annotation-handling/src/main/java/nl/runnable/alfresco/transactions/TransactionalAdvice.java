package nl.runnable.alfresco.transactions;

import java.lang.reflect.Method;
import java.util.Set;

import nl.runnable.alfresco.transactions.annotations.Transactional;

import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;

/**
 * Provides advice for {@link Transactional}-annotated methods.
 * <p>
 * This implementation invokes {@link Transactional} operations within a {@link RetryingTransactionCallback}
 * 
 * @author Laurens Fridael
 * 
 */
public class TransactionalAdvice implements MethodInterceptor {

	/* Dependencies */

	private final RetryingTransactionHelper retryingTransactionHelper;

	/* Configuration */

	private final Set<Method> transactionalMethods;

	/* Main operations */

	/**
	 * Creates an instance using the given {@link RetryingTransactionHelper} for the given {@link Method}s that should
	 * have advice applied to them.
	 * 
	 * @param retryingTransactionHelper
	 * @param transactionalMethods
	 */
	TransactionalAdvice(final RetryingTransactionHelper retryingTransactionHelper,
			final Set<Method> transactionalMethods) {
		Assert.notNull(retryingTransactionHelper);
		Assert.notEmpty(transactionalMethods);
		this.retryingTransactionHelper = retryingTransactionHelper;
		this.transactionalMethods = transactionalMethods;
	}

	@Override
	public Object invoke(final MethodInvocation invocation) throws Throwable {
		final Method method = invocation.getMethod();
		if (transactionalMethods.contains(method)) {
			return invokeTransactionalMethod(method, invocation.getThis(), invocation.getArguments());
		} else {
			return method.invoke(invocation.getThis(), invocation.getArguments());
		}
	}

	/* Utility operations */

	protected Object invokeTransactionalMethod(final Method method, final Object target, final Object[] arguments) {
		/* TODO: Determine if the performance impact of finding an annotation is significant enough to warrant caching. */
		final Transactional transactional = AnnotationUtils.findAnnotation(method, Transactional.class);
		return retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Object>() {

			@Override
			public Object execute() throws Throwable {
				return method.invoke(target, arguments);
			}
		}, transactional.readOnly(), transactional.requiresNew());
	}
}
