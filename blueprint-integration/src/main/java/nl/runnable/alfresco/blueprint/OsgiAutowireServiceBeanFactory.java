package nl.runnable.alfresco.blueprint;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Named;

import nl.runnable.alfresco.annotations.AlfrescoService;
import nl.runnable.alfresco.annotations.ServiceType;

import org.alfresco.service.cmr.repository.NodeService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;

/**
 * {@link BeanFactory} that resolves autowire candidates against OSGI services.
 * 
 * @author Laurens Fridael
 * 
 */
class OsgiAutowireServiceBeanFactory extends DefaultListableBeanFactory {

	private static final String HOST_APPLICATION_CONTEXT_BEAN_NAME = "HostApplicationContext";

	private static final String OSGI_SERVICE_BLUEPRINT_COMPNAME = "osgi.service.blueprint.compname";

	private static final String ALFRESCO_SERVICE_TYPE = "alfresco.service.type";

	private final BundleContext bundleContext;

	OsgiAutowireServiceBeanFactory(final BeanFactory parentBeanFactory, final BundleContext bundleContext) {
		super(parentBeanFactory);
		this.bundleContext = bundleContext;
	}

	/* Main operations */

	/**
	 * Finds autowire candidate beans by resolving OSGi services using required type's Java class name.
	 * <p>
	 * For example, if the required type is {@link NodeService}, this implementation looks for a service named
	 * <code>org.alfresco.service.cmr.repository.NodeService</code>.
	 * <p>
	 * This implementation also supports the {@link Named} annotation on a dependency to directly reference named beans
	 * in the Alfresco application context. This support enables you to distinguish between multiple implementations of
	 * the same interface. One example would be referring the <code>categoryService</code> or the
	 * <code>CategoryService</code>. (The latter bean is a proxy over the first bean, with additional support for
	 * auditing.)
	 */
	@Override
	@SuppressWarnings("rawtypes")
	protected Map<String, Object> findAutowireCandidates(final String beanName, final Class requiredType,
			final DependencyDescriptor descriptor) {
		Map<String, Object> candidateBeansByName = new HashMap<String, Object>();
		final String typeName = requiredType.getName();
		if (BundleContext.class.isAssignableFrom(requiredType)) {
			candidateBeansByName.put(typeName, bundleContext);
		} else {
			final ServiceReference<?> serviceReference = findServiceReference(descriptor, typeName);
			if (serviceReference != null) {
				candidateBeansByName.put(typeName, bundleContext.getService(serviceReference));
			} else {
				final Named named = getAnnotation(descriptor, Named.class);
				if (named != null) {
					final Object bean = findBeanInAlfrescoApplicationContext(named.value());
					if (bean != null) {
						candidateBeansByName.put(named.value(), bean);
					}
				}
			}
		}
		if (candidateBeansByName.isEmpty()) {
			candidateBeansByName = super.findAutowireCandidates(beanName, requiredType, descriptor);
		}
		return candidateBeansByName;
	}

	/**
	 * Attempts to find a named bean in the Alfresco {@link ApplicationContext}.
	 * 
	 * @param beanName
	 * @return
	 */
	protected Object findBeanInAlfrescoApplicationContext(final String beanName) {
		Object bean = null;
		final ServiceReference<?> serviceReference = getServiceReferenceWithBeanName(
				ApplicationContext.class.getName(), HOST_APPLICATION_CONTEXT_BEAN_NAME);
		if (serviceReference != null) {
			final ApplicationContext applicationContext = (ApplicationContext) bundleContext
					.getService(serviceReference);
			bean = applicationContext.getBean(beanName);
		}
		return bean;
	}

	/* Utility operations */

	protected ServiceReference<?> findServiceReference(final DependencyDescriptor descriptor, final String serviceName) {
		final ServiceReference<?> serviceReference;
		final AlfrescoService alfrescoService = getAnnotation(descriptor, AlfrescoService.class);
		final Named named = getAnnotation(descriptor, Named.class);
		if (alfrescoService != null || named != null) {
			if (alfrescoService != null) {
				serviceReference = getServiceReferenceForServiceType(serviceName, alfrescoService.value());
			} else {
				serviceReference = getServiceReferenceWithBeanName(serviceName, named.value());
			}
		} else {
			serviceReference = getServiceReferenceByName(serviceName);
		}
		return serviceReference;
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

	private ServiceReference<?> getServiceReferenceForServiceType(final String serviceName,
			final ServiceType serviceType) {
		try {
			final ServiceReference<?>[] serviceReferences = bundleContext.getServiceReferences(serviceName,
					filterServiceForServiceType(serviceType));
			if (serviceReferences != null && serviceReferences.length > 0) {
				return serviceReferences[0];
			} else {
				return null;
			}
		} catch (final InvalidSyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	private String filterServiceForServiceType(final ServiceType serviceType) {
		final String filter;
		switch (serviceType) {
		case DEFAULT:
			// Fall through
		default:
			filter = String.format("(%s=%s)", ALFRESCO_SERVICE_TYPE, "default");
			break;
		case LOW_LEVEL:
			filter = String.format("(%s=%s)", ALFRESCO_SERVICE_TYPE, "low-level");
			break;
		}
		return filter;
	}

	private ServiceReference<?> getServiceReferenceWithBeanName(final String serviceName, final String beanName) {
		try {
			final String filter = String.format("(%s=%s)", OSGI_SERVICE_BLUEPRINT_COMPNAME, beanName);
			final ServiceReference<?>[] serviceReferences = bundleContext.getServiceReferences(serviceName, filter);
			if (serviceReferences != null && serviceReferences.length > 0) {
				return serviceReferences[0];
			} else {
				return null;
			}
		} catch (final InvalidSyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	private ServiceReference<?> getServiceReferenceByName(final String serviceName) {
		ServiceReference<?> serviceReference = getServiceReferenceForServiceType(serviceName, ServiceType.DEFAULT);
		if (serviceReference == null) {
			serviceReference = bundleContext.getServiceReference(serviceName);
		}
		return serviceReference;
	}

}
