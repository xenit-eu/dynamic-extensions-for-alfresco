package com.github.dynamicextensionsalfresco.osgi.spring;

import com.github.dynamicextensionsalfresco.BeanNames;
import com.github.dynamicextensionsalfresco.annotations.AlfrescoService;
import com.github.dynamicextensionsalfresco.annotations.ServiceType;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * {@link BeanFactory} that augments default autowiring logic by attempting to resolve dependencies using Alfresco
 * naming conventions.
 * 
 * This class is overwritten for older versions of Alfresco.
 * When making changes here, make sure you also take a look to the implementation for older Alfresco versions.
 *
 * @author Laurens Fridael
 *
 */
public class AutowireBeanFactory extends DefaultListableBeanFactory {
    private final Set<String> internalBeanNames = new HashSet<String>();

    /* Main operations */

    public AutowireBeanFactory(final BeanFactory parentBeanFactory) {
        super(parentBeanFactory);

        for (BeanNames beanName : BeanNames.values()) {
            internalBeanNames.add(beanName.id());
        }
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
    private <T extends Annotation> T getAnnotation(final DependencyDescriptor descriptor, final Class<T> annotationType) {
        for (final Annotation annotation : descriptor.getAnnotations()) {
            if (annotationType.isAssignableFrom(annotation.annotationType())) {
                return (T) annotation;
            }
        }
        return null;
    }
}
