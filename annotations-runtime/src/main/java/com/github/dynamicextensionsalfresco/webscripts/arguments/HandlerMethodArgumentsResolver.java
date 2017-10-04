package com.github.dynamicextensionsalfresco.webscripts.arguments;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Strategy for resolving handler method arguments.
 * 
 * @author Laurens Fridael
 * @author Laurent Van der Linden
 * @see ArgumentResolver
 */
public class HandlerMethodArgumentsResolver implements ApplicationContextAware {

	/* Dependencies */

	private StringValueConverter stringValueConverter;

    private BundleContext bundleContext;

	/* Configuration */

	private List<ArgumentResolver<Object, Annotation>> argumentResolvers;

	private final Map<ArgumentResolverKey, ArgumentResolver<Object, Annotation>> argumentResolverCache = new ConcurrentHashMap<ArgumentResolverKey, ArgumentResolver<Object, Annotation>>();

	private final ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
    private ServiceTracker resolverTracker;
    private ApplicationContext applicationContext;

	/* Main Operations */

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void initializeArgumentResolvers() {
		argumentResolvers = new ArrayList<ArgumentResolver<Object, Annotation>>();
		argumentResolvers.add((ArgumentResolver) new RequestParamArgumentResolver(getStringValueConverter()));
		argumentResolvers.add((ArgumentResolver) new UriVariableArgumentResolver(getStringValueConverter()));
		argumentResolvers.add((ArgumentResolver) new AttributeArgumentResolver());
		argumentResolvers.add((ArgumentResolver) new ModelArgumentResolver());
		argumentResolvers.add((ArgumentResolver) new ExceptionArgumentResolver());
		argumentResolvers.add((ArgumentResolver) new HeaderArgumentResolver());
		argumentResolvers.add((ArgumentResolver) new ContentArgumentResolver());
		argumentResolvers.add((ArgumentResolver) new CommandArgumentResolver());
		argumentResolvers.add((ArgumentResolver) new FileFieldArgumentResolver());
		argumentResolvers.add((ArgumentResolver) new WebScriptRequestArgumentResolver());
		argumentResolvers.add((ArgumentResolver) new WebScriptResponseArgumentResolver());
		argumentResolvers.add((ArgumentResolver) new WebScriptSessionArgumentResolver());
		argumentResolvers.add((ArgumentResolver) new HttpServletRequestArgumentResolver());
		argumentResolvers.add((ArgumentResolver) new HttpServletResponseArgumentResolver());
		argumentResolvers.add((ArgumentResolver) new JsonObjectArgumentResolver());

        if (bundleContext != null) {
            resolverTracker = new ServiceTracker(bundleContext, ArgumentResolver.class, null);
            resolverTracker.open(true);
        }
    }

    /**
	 * Resolves the arguments of the given handler method.
	 * 
	 * @param method
	 *            The handler method.
	 * @param handler
	 *            The handler itself.
	 * @param request The webscript request
	 * @param response The webscript response
	 * @return The arguments to invoke handlerMethod with.
	 */
	public Object[] resolveHandlerMethodArguments(Method method, final Object handler, final WebScriptRequest request,
			final WebScriptResponse response) {
		Assert.notNull(method, "Method cannot be null.");
		Assert.notNull(request, "Request cannot be null.");
		Assert.notNull(response, "Response cannot be null.");

		final Class<?>[] parameterTypes = method.getParameterTypes();
		final Object[] arguments = new Object[parameterTypes.length];
		if (AopUtils.isAopProxy(handler)) {
			method = AopUtils.getMostSpecificMethod(method, AopUtils.getTargetClass(handler));
		}
		final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
		final String[] methodParameterNames = parameterNameDiscoverer.getParameterNames(method);
		for (int index = 0; index < parameterTypes.length; index++) {
			Annotation mappingAnnotation = null;
			ArgumentResolver<Object, Annotation> argumentResolver = null;
			if (parameterAnnotations[index].length == 0) {
				argumentResolver = getArgumentResolver(parameterTypes[index], null);
			} else {
				for (final Annotation parameterAnnotation : parameterAnnotations[index]) {
					argumentResolver = getArgumentResolver(parameterTypes[index], parameterAnnotation.annotationType());
					if (argumentResolver != null) {
						mappingAnnotation = parameterAnnotation;
						break;
					}
				}
			}
			if (argumentResolver == null) {
				throw new IllegalStateException(String.format("Cannot map parameter at index %d of method %s.%s.", index,
						handler.getClass().getSimpleName(), method.getName()));
			}
			String name = null;
			if (methodParameterNames != null) {
				name = methodParameterNames[index];
			}
			arguments[index] = argumentResolver.resolveArgument(parameterTypes[index], mappingAnnotation, name,
					request, response);
		}
		return arguments;
	}

	/* Utility Operations */

	@SuppressWarnings("unchecked")
    protected ArgumentResolver<Object, Annotation> getArgumentResolver(final Class<?> parameterType,
			final Class<? extends Annotation> annotationType) {
		Assert.notNull(parameterType, "ParameterType cannot be null.");

		final ArgumentResolverKey cacheKey = new ArgumentResolverKey(parameterType, annotationType);
        final ArgumentResolver<Object, Annotation> match = argumentResolverCache.get(cacheKey);
		if (match != null) {
            return match;
		}

        // spring component resolvers
        final Map<String, ArgumentResolver> localArgumentResolvers = applicationContext.getBeansOfType(ArgumentResolver.class);
        for (ArgumentResolver argumentResolver : localArgumentResolvers.values()) {
            if (argumentResolver.supports(parameterType, annotationType)) {
                argumentResolverCache.put(cacheKey, argumentResolver);
                return argumentResolver;
            }
        }

        // osgi resolvers
        if (resolverTracker != null) {
            // check for Osgi additions
            final Object[] services = resolverTracker.getServices();
            if (services != null) {
                for (Object service : services) {
                    final ArgumentResolver<Object,Annotation> argumentResolver = (ArgumentResolver<Object,Annotation>)service;
                    if (argumentResolver.supports(parameterType, annotationType)) {
                        // cannot cache these due to dynamic nature
                        return argumentResolver;
                    }
                }
            }
        }

		// static resolvers
		for (final ArgumentResolver<Object, Annotation> argumentResolver : argumentResolvers) {
			if (argumentResolver.supports(parameterType, annotationType)) {
				argumentResolverCache.put(cacheKey, argumentResolver);
				return argumentResolver;
			}
		}

        return null;
	}

	/* Dependencies */

	public void setStringValueConverter(final StringValueConverter stringValueConverter) {
		this.stringValueConverter = stringValueConverter;
	}

	protected StringValueConverter getStringValueConverter() {
		return stringValueConverter;
	}

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private static class ArgumentResolverKey {
        public final Class parameterType;
        public final Class annotationType;

        private ArgumentResolverKey(Class parameterType, Class annotationType) {
            this.parameterType = parameterType;
            this.annotationType = annotationType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ArgumentResolverKey that = (ArgumentResolverKey) o;

            return !(annotationType != null ? !annotationType.equals(that.annotationType) : that.annotationType != null) && parameterType.equals(that.parameterType);
        }

        @Override
        public int hashCode() {
            int result = parameterType.hashCode();
            result = 31 * result + (annotationType != null ? annotationType.hashCode() : 0);
            return result;
        }
    }
}
