package nl.runnable.alfresco.aop;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.util.Assert;

public abstract class AbstractMethodAdvice implements MethodInterceptor {

	/* State */

	private final Set<Method> methods;

	/* Main operations */

	AbstractMethodAdvice(final Set<Method> methods) {
		Assert.notEmpty(methods);
		this.methods = methods;
	}

	@Override
	public final Object invoke(final MethodInvocation invocation) throws Throwable {
		final Method method = invocation.getMethod();
		if (methods.contains(method)) {
			return proceedWithAdvice(invocation);
		} else {
			return invocation.proceed();
		}
	}

	/**
	 * Utility function for proceeding with the given {@link MethodInvocation}. This operation is intended be called by
	 * implementations of {@link #proceedWithAdvice(MethodInvocation)}.
	 * 
	 * @param invocation
	 * @return
	 * @throws InvocationTargetException
	 */
	protected final Object proceed(final MethodInvocation invocation) throws InvocationTargetException {
		try {
			return invocation.proceed();
		} catch (final Throwable e) {
			throw new InvocationTargetException(e);
		}
	}

	/**
	 * Proceeds the given {@link MethodInvocation} with the advice provided by this class.
	 * 
	 * @param invocation
	 * @return
	 * @throws Throwable
	 */
	protected abstract Object proceedWithAdvice(MethodInvocation invocation) throws Throwable;

}
