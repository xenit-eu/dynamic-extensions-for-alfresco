package com.github.dynamicextensionsalfresco.webscripts.arguments;

import java.lang.annotation.Annotation;
import java.util.Map;

import com.github.dynamicextensionsalfresco.webscripts.AnnotationWebScriptRequest;

import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * Resolves {@link Map} handler method arguments to the model.
 * 
 * @author Laurens Fridael
 * 
 */
public class ModelArgumentResolver implements ArgumentResolver<Map<String, Object>, Annotation> {

	@Override
	public boolean supports(final Class<?> parameterType, final Class<? extends Annotation> annotationType) {
		return Map.class.equals(parameterType);
	}

	@Override
	public Map<String, Object> resolveArgument(final Class<?> argumentType, final Annotation parameterAnnotation,
			final String name, final WebScriptRequest request, final WebScriptResponse response) {
		return ((AnnotationWebScriptRequest) request).getModel();
	}

}
