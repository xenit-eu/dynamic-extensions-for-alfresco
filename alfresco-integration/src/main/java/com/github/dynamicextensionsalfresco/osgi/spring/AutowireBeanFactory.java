package com.github.dynamicextensionsalfresco.osgi.spring;

import java.lang.annotation.Annotation;
import java.util.Map;

import com.github.dynamicextensionsalfresco.annotations.AlfrescoService;
import com.github.dynamicextensionsalfresco.annotations.ServiceType;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * {@link BeanFactory} that augments default autowiring logic by attempting to resolve dependencies using Alfresco
 * naming conventions.
 * 
 * @author Laurens Fridael
 * 
 */
public class AutowireBeanFactory extends DefaultListableBeanFactory {

	/* Main operations */

	public AutowireBeanFactory(final BeanFactory parentBeanFactory) {
		super(parentBeanFactory);
	}

	@Override
	protected String determinePrimaryCandidate(final Map<String, Object> candidateBeans,
			final DependencyDescriptor descriptor) {
		String beanName = ClassUtils.getShortName(descriptor.getDependencyType());
		final AlfrescoService alfrescoService = getAnnotation(descriptor, AlfrescoService.class);
		final ServiceType serviceType = alfrescoService != null ? alfrescoService.value() : ServiceType.DEFAULT;
		switch (serviceType) {
		default:
			// Fall through
		case DEFAULT:
			if (candidateBeans.containsKey(beanName)) {
				return beanName;
			}
			// Fall through
		case LOW_LEVEL:
			beanName = StringUtils.uncapitalize(beanName);
			if (candidateBeans.containsKey(beanName)) {
				return beanName;
			}
			break;
		}
		return super.determinePrimaryCandidate(candidateBeans, descriptor);
	}

	/* Utility operations */

	@SuppressWarnings("unchecked")
	private <T extends Annotation> T getAnnotation(final DependencyDescriptor descriptor, final Class<T> annotationType) {
		for (final Annotation annotation : descriptor.getAnnotations()) {
			if (annotationType.isAssignableFrom(annotation.annotationType())) {
				return (T) annotation;
			}
		}
		return null;
	}
}
