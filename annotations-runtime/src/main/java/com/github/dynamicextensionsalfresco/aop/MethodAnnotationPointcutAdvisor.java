package com.github.dynamicextensionsalfresco.aop;

import java.lang.annotation.Annotation;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

/**
 * Convenience base class for {@link PointcutAdvisor}s that advise methods annotated with a given annotation.
 * 
 * @author Laurens Fridael
 * 
 */
public class MethodAnnotationPointcutAdvisor implements PointcutAdvisor, DynamicExtensionsAdvisor {

	/* Configuration */

	private final Pointcut pointcut;

	private Advice advice;

	/* Main operations */

	/**
	 * Constructs an instance using a {@link MethodAnnotationPointcut} for the given {@link Annotation} type.
	 * 
	 * @param annotationType
	 */
	protected MethodAnnotationPointcutAdvisor(final Class<? extends Annotation> annotationType) {
		Assert.notNull(annotationType);
		pointcut = new MethodAnnotationPointcut(annotationType);
	}

	@Override
	public Pointcut getPointcut() {
		return pointcut;
	}

	@Override
	public boolean isPerInstance() {
		return false;
	}

	@Override
	public Advice getAdvice() {
		Assert.state(advice != null, "Advice has not been configured.");
		return advice;
	}

	/* Configuration */

	@Required
	public void setAdvice(final Advice advice) {
		Assert.notNull(advice);
		this.advice = advice;
	}

}
