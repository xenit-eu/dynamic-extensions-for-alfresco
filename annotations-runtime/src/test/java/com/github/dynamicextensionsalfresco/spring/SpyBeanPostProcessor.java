package com.github.dynamicextensionsalfresco.spring;

import org.mockito.Mockito;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;

/**
 * Creates {@link Mockito} spies for beans of a given type or with a given annotation.
 * 
 * @author Laurens Fridael
 * 
 */
public class SpyBeanPostProcessor implements BeanPostProcessor {

	/* Configuration */

	private Class<? extends Annotation> annotation = Spied.class;

	private Class<?> clazz;

	/* Main operations */

	@Override
	public Object postProcessBeforeInitialization(Object bean, final String beanName) throws BeansException {
		if (hasAnnotation(bean) || isOfType(bean)) {
			bean = Mockito.spy(bean);
		}
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
		return bean;
	}

	/* Utility operations */

	private boolean hasAnnotation(final Object bean) {
		return annotation != null && AnnotationUtils.findAnnotation(bean.getClass(), annotation) != null;
	}

	private boolean isOfType(final Object bean) {
		return clazz != null && clazz.isInstance(bean);
	}

	/* Configuration */

	public void setAnnotation(final Class<? extends Annotation> annotationClass) {
		this.annotation = annotationClass;
	}

	public void setClass(final Class<? extends Annotation> clazz) {
		this.clazz = clazz;
	}

}
