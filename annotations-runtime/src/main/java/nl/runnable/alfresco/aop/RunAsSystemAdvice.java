package nl.runnable.alfresco.aop;

import java.lang.reflect.InvocationTargetException;

import nl.runnable.alfresco.annotations.RunAs;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * Provides advice for {@link RunAs}-annotated methods.
 * <p>
 * This implementation invokes {@link RunAs} operations within a {@link RunAsWork} callback.
 */
public class RunAsSystemAdvice implements MethodInterceptor {

	@Override
	public Object invoke(final MethodInvocation invocation) throws Throwable {
		return AuthenticationUtil.runAs(new RunAsWork<Object>() {

			@Override
			public Object doWork() throws Exception {
				try {
					return invocation.proceed();
				} catch (final Throwable e) {
					throw new InvocationTargetException(e);
				}
			}
		}, AuthenticationUtil.getSystemUserName());
	}
}
