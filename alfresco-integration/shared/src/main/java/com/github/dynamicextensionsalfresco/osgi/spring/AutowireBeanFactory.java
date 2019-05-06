package com.github.dynamicextensionsalfresco.osgi.spring;

import com.github.dynamicextensionsalfresco.BeanNames;
import com.github.dynamicextensionsalfresco.annotations.AlfrescoService;
import com.github.dynamicextensionsalfresco.annotations.ServiceType;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
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
 * @author Laurens Fridael
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

    // @Override for Spring < 5
    protected String determinePrimaryCandidate(final Map<String, Object> candidateBeans,
            final DependencyDescriptor descriptor) {
        final String candidateBean = tryToDetermineBean(candidateBeans, descriptor);
        if (candidateBean != null) {
            return candidateBean;
        }

        return this.tryToInvokeSuperMethod("determinePrimaryCandidate", candidateBeans, descriptor);
    }

    // @Override for Spring >= 5
    protected String determineAutowireCandidate(final Map<String, Object> candidateBeans,
            final DependencyDescriptor descriptor) {
        final String candidateBean = tryToDetermineBean(candidateBeans, descriptor);
        if (candidateBean != null) {
            return candidateBean;
        }

        return this.tryToInvokeSuperMethod("determineAutowireCandidate", candidateBeans, descriptor);
    }

    private String tryToDetermineBean(final Map<String, Object> candidateBeans,
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

        return null;
    }

    private String tryToInvokeSuperMethod(final String methodName, Object... args) {
        try {
            Class<?>[] argsClasses = Arrays.stream(args).map(Object::getClass).toArray(Class<?>[]::new);
            Method m = super.getClass().getDeclaredMethod(methodName, argsClasses);
            return (String) m.invoke(this, args);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalStateException("Unable to call '" + methodName + "' super method", e);
        }
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
