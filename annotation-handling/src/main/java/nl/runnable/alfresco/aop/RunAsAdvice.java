package nl.runnable.alfresco.aop;

import java.lang.reflect.Method;
import java.util.Set;

import nl.runnable.alfresco.aop.annotations.RunAs;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * Provides advice for {@link RunAs}-annotated methods.
 * <p>
 * This implementation invokes {@link RunAs} operations within a {@link RunAsWork} callback.
 */
public class RunAsAdvice extends AbstractMethodAdvice {

	RunAsAdvice(final Set<Method> methods) {
		super(methods);
	}

	/* Utility operations */

	@Override
	protected Object proceedWithAdvice(final MethodInvocation invocation) {
		final String user = AnnotationUtils.findAnnotation(invocation.getMethod(), RunAs.class).value();
		return AuthenticationUtil.runAs(new RunAsWork<Object>() {

			@Override
			public Object doWork() throws Exception {
				return proceed(invocation);
			}
		}, user);
	}

}
