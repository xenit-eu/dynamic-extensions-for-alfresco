package nl.runnable.alfresco.osgi.spring;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import nl.runnable.alfresco.annotations.AlfrescoService;
import nl.runnable.alfresco.annotations.ServiceType;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import javax.inject.Named;

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

	@Override
	protected Map<String, Object> findAutowireCandidates(String beanName, Class requiredType, DependencyDescriptor descriptor) {
		Map<String,Object> candidateBeansByName = null;
		final Named named = getAnnotation(descriptor, Named.class);
		if (named != null) {
			candidateBeansByName = putAnyNamedBean(named.value());
		} else {
			final Qualifier qualifier = getAnnotation(descriptor, Qualifier.class);
			if (qualifier != null) {
				candidateBeansByName = putAnyNamedBean(qualifier.value());
			}
		}

		if (candidateBeansByName != null) {
			return candidateBeansByName;
		} else {
			return super.findAutowireCandidates(beanName, requiredType, descriptor);
		}
	}

	/* Utility operations */

	private Map<String,Object> putAnyNamedBean(String name) {
		if (super.containsBean(name)) {
			final Map<String,Object> candidateBeansByName = new HashMap<String, Object>(1);
			candidateBeansByName.put(name, super.getBean(name));
			return candidateBeansByName;
		}
		return null;
	}

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
