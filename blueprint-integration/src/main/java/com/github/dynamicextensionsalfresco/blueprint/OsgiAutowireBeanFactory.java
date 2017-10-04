package com.github.dynamicextensionsalfresco.blueprint;

import com.github.dynamicextensionsalfresco.osgi.spring.AutowireBeanFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;

import java.util.Collections;
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
		Map<String, Object> candidateBeansByName = Collections.emptyMap();
		if (BundleContext.class.isAssignableFrom(requiredType)) {
			candidateBeansByName = new HashMap<String, Object>(1);
			candidateBeansByName.put(requiredType.getName(), bundleContext);
		} else {
			final ServiceReference serviceReference = bundleContext.getServiceReference(requiredType);
			if (serviceReference != null) {
				candidateBeansByName = new HashMap<String, Object>(1);
				candidateBeansByName.put(requiredType.getName(), bundleContext.getService(serviceReference));
			}
		}
		if (candidateBeansByName.isEmpty()) {
			candidateBeansByName = super.findAutowireCandidates(beanName, requiredType, descriptor);
		}
		return candidateBeansByName;
	}

}
