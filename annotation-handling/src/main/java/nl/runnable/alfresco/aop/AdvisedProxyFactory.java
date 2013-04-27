package nl.runnable.alfresco.aop;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedHashSet;
import java.util.Set;

import nl.runnable.alfresco.aop.annotations.Transactional;

import org.aopalliance.aop.Advice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxyFactory;
import org.springframework.aop.framework.DefaultAopProxyFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.MethodCallback;

/**
 * Factory for creating AOP proxies for beans with {@link Transactional}-annotated methods.
 * 
 * @author Laurens Fridael
 * 
 */
public class AdvisedProxyFactory {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	/* Dependencies */

	private AopProxyFactory aopProxyFactory = new DefaultAopProxyFactory();

	private Set<AdviceResolver> adviceResolvers;

	/* Main operations */

	public AdvisedProxyFactory(final Set<AdviceResolver> adviceResolvers) {
		Assert.notNull(adviceResolvers);
		this.adviceResolvers = adviceResolvers;
	}

	public AdvisedProxyFactory() {
	}

	@SuppressWarnings("unchecked")
	public boolean hasMethodWithAnySupportedAnnotation(final Object target) {
		Assert.notNull(target);
		boolean hasMethods = false;
		for (final AdviceResolver adviceResolver : getAdviceResolvers()) {
			if (hasMethodsAnnotatedWith(target, adviceResolver.getAnnotationType())) {
				hasMethods = true;
				break;
			}
		}
		return hasMethods;
	}

	/**
	 * Tests if the given target has methods annotated with any of the given {@link Annotation} types.
	 * 
	 * @param target
	 * @param annotationTypes
	 * @return
	 */
	public boolean hasMethodsAnnotatedWith(final Object target, final Class<? extends Annotation>... annotationTypes) {
		Assert.notNull(target);
		return (findMethodsAnnotatedWith(target, annotationTypes).isEmpty() == false);
	}

	/**
	 * Creates an AOP proxy for the given target. This implementation
	 * 
	 * @param target
	 * @return The AOP proxy.
	 */
	@SuppressWarnings("unchecked")
	public <T> T createAdvisedProxy(final T target) {
		Assert.notNull(target);
		final AdvisedSupport config = new AdvisedSupport();
		config.setTarget(target);
		for (final AdviceResolver adviceResolver : getAdviceResolvers()) {
			final Set<Method> methods = findMethodsAnnotatedWith(target, adviceResolver.getAnnotationType());
			if (methods.isEmpty() == false) {
				final Advice advice = adviceResolver.getAdvice(methods);
				config.addAdvice(advice);
			}
		}
		return ((T) getAopProxyFactory().createAopProxy(config).getProxy());
	}

	/**
	 * Obtains the {@link Method}s annotated with {@link Transactional} for the given target.
	 * 
	 * @param target
	 * @return
	 */
	protected Set<Method> findMethodsAnnotatedWith(final Object target,
			final Class<? extends Annotation>... annotationTypes) {
		final Set<Method> methods = new LinkedHashSet<Method>();
		ReflectionUtils.doWithMethods(target.getClass(), new MethodCallback() {

			@Override
			public void doWith(final Method method) throws IllegalArgumentException, IllegalAccessException {
				for (final Class<? extends Annotation> annotationType : annotationTypes) {
					if (AnnotationUtils.findAnnotation(method, annotationType) != null) {
						if ((method.getModifiers() & Modifier.PUBLIC) > 0) {
							if (methods.contains(method) == false) {
								methods.add(method);
							}
						} else {
							logger.warn(String.format("Found AOP annotation '%s' on non-public method '%s'. "
									+ "AOP annotations can only be used on public methods.", annotationType.getName(),
									ClassUtils.getQualifiedMethodName(method)));
						}
					}
				}
			}
		});
		return methods;
	}

	/* Dependencies */

	public void setAopProxyFactory(final AopProxyFactory aopProxyFactory) {
		Assert.notNull(aopProxyFactory);
		this.aopProxyFactory = aopProxyFactory;
	}

	protected AopProxyFactory getAopProxyFactory() {
		return aopProxyFactory;
	}

	public void setAdviceResolvers(final Set<AdviceResolver> adviceResolvers) {
		this.adviceResolvers = adviceResolvers;
	}

	protected Set<AdviceResolver> getAdviceResolvers() {
		return adviceResolvers;
	}

}
