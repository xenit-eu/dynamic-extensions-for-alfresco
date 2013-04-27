package nl.runnable.alfresco.aop;

import java.lang.reflect.Method;
import java.util.Set;

import nl.runnable.alfresco.aop.annotations.RunAs;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.aopalliance.intercept.MethodInvocation;

/**
 * Provides advice for {@link RunAs}-annotated methods.
 * <p>
 * This implementation invokes {@link RunAs} operations within a {@link RunAsWork} callback.
 */
public class RunAsSystemAdvice extends AbstractMethodAdvice {

	RunAsSystemAdvice(final Set<Method> methods) {
		super(methods);
	}

	/* Utility operations */

	@Override
	protected Object proceedWithAdvice(final MethodInvocation invocation) {
		return AuthenticationUtil.runAsSystem(new RunAsWork<Object>() {

			@Override
			public Object doWork() throws Exception {
				return proceed(invocation);
			}
		});
	}

}
