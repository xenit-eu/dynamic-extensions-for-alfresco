package nl.runnable.alfresco.aop;

import java.lang.reflect.Method;
import java.util.Set;

import nl.runnable.alfresco.aop.annotations.Transactional;

import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
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
public class TransactionalAdvice extends AbstractMethodAdvice {

	/* Dependencies */

	private final RetryingTransactionHelper retryingTransactionHelper;

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
		super(transactionalMethods);
		Assert.notNull(retryingTransactionHelper);
		this.retryingTransactionHelper = retryingTransactionHelper;
	}

	@Override
	protected Object proceedWithAdvice(final MethodInvocation invocation) {
		/* TODO: Determine if the performance impact of finding an annotation is significant enough to warrant caching. */
		final Transactional transactional = AnnotationUtils.findAnnotation(invocation.getMethod(), Transactional.class);
		return retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Object>() {

			@Override
			public Object execute() throws Throwable {
				return proceed(invocation);
			}
		}, transactional.readOnly(), transactional.requiresNew());
	}
}
