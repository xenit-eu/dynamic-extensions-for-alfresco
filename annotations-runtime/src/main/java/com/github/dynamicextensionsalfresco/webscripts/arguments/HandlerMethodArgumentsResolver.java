package com.github.dynamicextensionsalfresco.webscripts.arguments;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.springframework.aop.support.AopUtils;
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
 * @see ArgumentResolver
 */
public class HandlerMethodArgumentsResolver {

	/* Dependencies */

	private StringValueConverter stringValueConverter;

    private BundleContext bundleContext;

	/* Configuration */

	private List<ArgumentResolver<Object, Annotation>> argumentResolvers;

	private final Map<Integer, ArgumentResolver<Object, Annotation>> argumentResolversByHashCode = new ConcurrentHashMap<Integer, ArgumentResolver<Object, Annotation>>();

	private final ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
    private ServiceTracker resolverTracker;

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
	 * @param request
	 * @param response
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
				throw new IllegalStateException(String.format("Cannot map parameter at index %d of method %s.", index,
						method.getName()));
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

	protected ArgumentResolver<Object, Annotation> getArgumentResolver(final Class<?> parameterType,
			final Class<? extends Annotation> annotationType) {
		Assert.notNull(parameterType, "ParameterType cannot be null.");

		final int hashCode = calculateHashCode(parameterType, annotationType);
		if (argumentResolversByHashCode.containsKey(hashCode)) {
			return argumentResolversByHashCode.get(hashCode);
		}
		for (final ArgumentResolver<Object, Annotation> argumentResolver : argumentResolvers) {
			if (argumentResolver.supports(parameterType, annotationType)) {
				argumentResolversByHashCode.put(hashCode, argumentResolver);
				return argumentResolver;
			}
		}

        if (resolverTracker != null) {
            // check for Osgi additions
            final Object[] services = resolverTracker.getServices();
            if (services != null) {
                for (Object service : services) {
                    @SuppressWarnings("unchecked")
                    final ArgumentResolver<Object,Annotation> argumentResolver = (ArgumentResolver<Object,Annotation>)service;
                    if (argumentResolver.supports(parameterType, annotationType)) {
                        // cannot cache these due to dynamic nature
                        return argumentResolver;
                    }
                }
            }
        }

        return null;
	}

	/**
	 * Calculates the hash code for an array of Classes. For internal use by {@link #getArgumentResolver(Class, Class)}
	 * 
	 * @param classes
	 * @return
	 */
	private static int calculateHashCode(final Class<?>... classes) {
		final int prime = 31;
		int result = 1;
		for (final Class<?> clazz : classes) {
			result = prime * result + (clazz != null ? clazz.hashCode() : 0);
		}
		return result;
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
}
