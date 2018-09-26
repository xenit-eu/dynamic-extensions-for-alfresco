package com.github.dynamicextensionsalfresco.aop;

import java.lang.reflect.InvocationTargetException;

import com.github.dynamicextensionsalfresco.annotations.RunAs;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * Provides advice for {@link RunAs}-annotated methods.
 * <p>
 * This implementation invokes {@link RunAs} operations within a {@link RunAsWork} callback.
 */
public class RunAsAdvice implements MethodInterceptor {

	@Override
	public Object invoke(final MethodInvocation invocation) throws Throwable {
		final String user = AnnotationUtils.findAnnotation(invocation.getMethod(), RunAs.class).value();
		return AuthenticationUtil.runAs(new RunAsWork<Object>() {

			@Override
			public Object doWork() throws Exception {
				try {
					return invocation.proceed();
				} catch (final Throwable e) {
					throw new InvocationTargetException(e);
				}
			}
		}, user);
	}

}
