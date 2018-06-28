package com.github.dynamicextensionsalfresco.aop;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.springframework.aop.Pointcut;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;

/**
 * {@link Pointcut} that matches methods with a given annotation.
 * <p>
 * This implementation uses {@link AnnotationUtils#findAnnotation(Method, Class)} to detect annotations on methods.
 * 
 * @author Laurens Fridael
 * 
 */
class MethodAnnotationPointcut extends StaticMethodMatcherPointcut {

	private final Class<? extends Annotation> annotationType;

	MethodAnnotationPointcut(final Class<? extends Annotation> annotationType) {
		Assert.notNull(annotationType);
		this.annotationType = annotationType;
	}

	@Override
	public boolean matches(final Method method, final Class<?> targetClass) {
		return AnnotationUtils.findAnnotation(method, annotationType) != null;
	}

}
