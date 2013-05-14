package nl.runnable.alfresco.blueprint;

import nl.runnable.alfresco.osgi.spring.AutowireBeanFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.DependencyDescriptor;

import javax.inject.Named;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

/**
 * Resolves dependencies on OSGi services or the {@link BundleContext}.
 * 
 * @author Laurens Fridael
 * 
 */
class OsgiAutowireBeanFactory extends AutowireBeanFactory {

	private final BundleContext bundleContext;

	OsgiAutowireBeanFactory(final BeanFactory parentBeanFactory, final BundleContext bundleContext) {
		super(parentBeanFactory);
		this.bundleContext = bundleContext;
	}

	/* Main operations */

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Map<String, Object> findAutowireCandidates(final String beanName, final Class requiredType,
			final DependencyDescriptor descriptor) {
		Map<String, Object> candidateBeansByName = new HashMap<String, Object>(1);
		if (BundleContext.class.isAssignableFrom(requiredType)) {
			candidateBeansByName.put(requiredType.getName(), bundleContext);
		} else {
			final ServiceReference serviceReference = bundleContext.getServiceReference(requiredType);
			if (serviceReference != null) {
				candidateBeansByName.put(requiredType.getName(), bundleContext.getService(serviceReference));
			}
		}

		final Named named = getAnnotation(descriptor, Named.class);
		if (named != null) {
			putAnyNamedBean(candidateBeansByName, named.value());
		} else {
			final Qualifier qualifier = getAnnotation(descriptor, Qualifier.class);
			if (qualifier != null) {
				putAnyNamedBean(candidateBeansByName, qualifier.value());
			}
		}

		if (candidateBeansByName.isEmpty()) {
			candidateBeansByName = super.findAutowireCandidates(beanName, requiredType, descriptor);
		}
		return candidateBeansByName;
	}

	private void putAnyNamedBean(Map<String, Object> candidateBeansByName, String name) {
		if (super.containsBean(name)) {
			candidateBeansByName.put(name, super.getBean(name));
		}
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
