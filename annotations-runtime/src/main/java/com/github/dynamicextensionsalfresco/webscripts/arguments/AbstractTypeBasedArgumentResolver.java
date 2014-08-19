package com.github.dynamicextensionsalfresco.webscripts.arguments;

import java.lang.annotation.Annotation;

import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * Convenenience adapter for {@link ArgumentResolver} implementations that rely solely on the parameter type and do not
 * allow parameter annotations.
 * 
 * @author Laurens Fridael
 * 
 * @param <ArgumentType>
 */
public abstract class AbstractTypeBasedArgumentResolver<ArgumentType> implements
		ArgumentResolver<ArgumentType, Annotation> {

	@Override
	public final boolean supports(final Class<?> parameterType, final Class<? extends Annotation> annotationType) {
		/* Determine if using Class.isAssignableFrom() breaks backwards compatibility. */
		return parameterType.equals(getExpectedArgumentType());
	}

	@Override
	public final ArgumentType resolveArgument(final Class<?> argumentType, final Annotation parameterAnnotation,
			final String name, final WebScriptRequest request, final WebScriptResponse response) {
		final Class<?> expectedParameterType = getExpectedArgumentType();
		if (argumentType.equals(expectedParameterType) == false) {
			throw new IllegalArgumentException(String.format("Incorrect parameter type %s, expected type %s",
					argumentType, expectedParameterType));
		}
		return resolveArgument(request, response);
	}

	protected abstract Class<?> getExpectedArgumentType();

	protected abstract ArgumentType resolveArgument(WebScriptRequest request, WebScriptResponse response);
}
