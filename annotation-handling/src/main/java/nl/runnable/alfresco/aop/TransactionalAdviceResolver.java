package nl.runnable.alfresco.aop;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

import nl.runnable.alfresco.aop.annotations.Transactional;

import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.aopalliance.aop.Advice;
import org.springframework.util.Assert;

public class TransactionalAdviceResolver implements AdviceResolver {

	/* Dependencies */

	private RetryingTransactionHelper retryingTransactionHelper;

	TransactionalAdviceResolver(final RetryingTransactionHelper retryingTransactionHelper) {
		this.retryingTransactionHelper = retryingTransactionHelper;
	}

	public TransactionalAdviceResolver() {
	}

	/* Main operations */

	@Override
	public Class<? extends Annotation> getAnnotationType() {
		return Transactional.class;
	}

	@Override
	public Advice getAdvice(final Set<Method> methods) {
		return new TransactionalAdvice(retryingTransactionHelper, methods);
	}

	/* Dependencies */

	public void setRetryingTransactionHelper(final RetryingTransactionHelper retryingTransactionHelper) {
		Assert.notNull(retryingTransactionHelper);
		this.retryingTransactionHelper = retryingTransactionHelper;
	}
}
