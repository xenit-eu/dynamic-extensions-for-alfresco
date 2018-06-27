package com.github.dynamicextensionsalfresco.aop;

import com.github.dynamicextensionsalfresco.annotations.Transactional;

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

	private RetryingTransactionHelper retryingTransactionHelper;

	/* Main operations */

	@Override
	public Object invoke(final MethodInvocation invocation) throws Throwable {
		final Transactional transactional = AnnotationUtils.findAnnotation(invocation.getMethod(), Transactional.class);
		return getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<Object>() {

			@Override
			public Object execute() throws Throwable {
				return invocation.proceed();
			}
		}, transactional.readOnly(), transactional.requiresNew());
	}

	/* Dependencies */

	public void setRetryingTransactionHelper(final RetryingTransactionHelper retryingTransactionHelper) {
		Assert.notNull(retryingTransactionHelper);
		this.retryingTransactionHelper = retryingTransactionHelper;
	}

	protected RetryingTransactionHelper getRetryingTransactionHelper() {
		return retryingTransactionHelper;
	}
}
