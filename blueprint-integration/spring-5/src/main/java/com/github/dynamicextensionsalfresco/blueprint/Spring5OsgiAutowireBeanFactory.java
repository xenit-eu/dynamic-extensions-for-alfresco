package com.github.dynamicextensionsalfresco.blueprint;

import com.github.dynamicextensionsalfresco.BeanNames;
import com.github.dynamicextensionsalfresco.annotations.AlfrescoService;
import com.github.dynamicextensionsalfresco.annotations.ServiceType;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * Spring 5 compatible {@link BeanFactory} that
 * <ul>
 * <li>Augments default autowiring logic by attempting to resolve dependencies using Alfresco naming conventions.</li>
 * <li>Resolves dependencies on OSGi services or the {@link BundleContext}</li>
 * </ul>
 *
 * @author Laurens Fridael
 */
public class Spring5OsgiAutowireBeanFactory extends DefaultListableBeanFactory {

    private final BundleContext bundleContext;
    private final Set<String> internalBeanNames = new HashSet<>();

    Spring5OsgiAutowireBeanFactory(final BeanFactory parentBeanFactory, final BundleContext bundleContext) {
        super(parentBeanFactory);
        this.bundleContext = bundleContext;

        for (BeanNames beanName : BeanNames.values()) {
            internalBeanNames.add(beanName.id());
        }
    }

    /* Main operations */

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected Map<String, Object> findAutowireCandidates(final String beanName, final Class requiredType,
            final DependencyDescriptor descriptor) {
        Map<String, Object> candidateBeansByName = Collections.emptyMap();
        if (BundleContext.class.isAssignableFrom(requiredType)) {
            candidateBeansByName = new HashMap<>(1);
            candidateBeansByName.put(requiredType.getName(), bundleContext);
        } else {
            final ServiceReference serviceReference = bundleContext.getServiceReference(requiredType);
            if (serviceReference != null) {
                candidateBeansByName = new HashMap<>(1);
                candidateBeansByName.put(requiredType.getName(), bundleContext.getService(serviceReference));
            }
        }
        if (candidateBeansByName.isEmpty()) {
            candidateBeansByName = super.findAutowireCandidates(beanName, requiredType, descriptor);
        }
        return candidateBeansByName;
    }

    @Override
    protected String determineAutowireCandidate(final Map<String, Object> candidateBeans,
            final DependencyDescriptor descriptor) {
        String beanName = ClassUtils.getShortName(descriptor.getDependencyType());

        for (String id : candidateBeans.keySet()) {
            if (internalBeanNames.contains(id)) {
                return id;
            }
        }

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
        return super.determineAutowireCandidate(candidateBeans, descriptor);
    }

    /* Utility operations */

    @SuppressWarnings("unchecked")
    private <T extends Annotation> T getAnnotation(final DependencyDescriptor descriptor,
            final Class<T> annotationType) {
        for (final Annotation annotation : descriptor.getAnnotations()) {
            if (annotationType.isAssignableFrom(annotation.annotationType())) {
                return (T) annotation;
            }
        }
        return null;
    }

}
