/*
Copyright (c) 2012, Runnable
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
 * Neither the name of Runnable nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package nl.runnable.alfresco.webscripts;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.util.Assert;

class DefaultHandlerMethodArgumentsResolver implements HandlerMethodArgumentsResolver {

	/* Configuration */

	private List<ArgumentResolver<Object, Annotation>> argumentResolvers;

	private final Map<Integer, ArgumentResolver<Object, Annotation>> argumentResolversByHashCode = new ConcurrentHashMap<Integer, ArgumentResolver<Object, Annotation>>();

	private final ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();

	/* Main Operations */

	public DefaultHandlerMethodArgumentsResolver() {
		initializeArgumentResolvers();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void initializeArgumentResolvers() {
		argumentResolvers = new ArrayList<ArgumentResolver<Object, Annotation>>();
		argumentResolvers.add((ArgumentResolver) new RequestParameterArgumentResolver());
		argumentResolvers.add((ArgumentResolver) new UriVariableArgumentResolver());
		argumentResolvers.add((ArgumentResolver) new WebScriptRequestArgumentResolver());
		argumentResolvers.add((ArgumentResolver) new WebScriptResponseArgumentResolver());
		argumentResolvers.add((ArgumentResolver) new WebScriptSessionArgumentResolver());
		argumentResolvers.add((ArgumentResolver) new WebScriptArgumentResolver());
		argumentResolvers.add((ArgumentResolver) new MatchArgumentResolver());
		argumentResolvers.add((ArgumentResolver) new RuntimeArgumentResolver());
		argumentResolvers.add((ArgumentResolver) new NodeRefArgumentResolver());
	}

	@Override
	public Object[] resolveHandlerMethodArguments(final Method method, final Object handler,
			final WebScriptRequest request, final WebScriptResponse response) {
		Assert.notNull(method, "Method cannot be null.");
		Assert.notNull(request, "Request cannot be null.");
		Assert.notNull(response, "Response cannot be null.");

		final Class<?>[] parameterTypes = method.getParameterTypes();
		final Object[] arguments = new Object[parameterTypes.length];
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
			arguments[index] = argumentResolver.resolveArgument(parameterTypes[index], mappingAnnotation, name, request,
					response);
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
		for (final ArgumentResolver<Object, Annotation> methodParameterMapper : argumentResolvers) {
			if (methodParameterMapper.supports(parameterType, annotationType)) {
				argumentResolversByHashCode.put(hashCode, methodParameterMapper);
				return methodParameterMapper;
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

}
