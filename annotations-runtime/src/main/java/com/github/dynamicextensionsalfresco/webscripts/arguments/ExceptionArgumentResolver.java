package com.github.dynamicextensionsalfresco.webscripts.arguments;

import com.github.dynamicextensionsalfresco.webscripts.AnnotationWebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import java.lang.annotation.Annotation;

public class ExceptionArgumentResolver implements ArgumentResolver<Throwable, Annotation> {

	@Override
	public final boolean supports(final Class<?> parameterType, final Class<? extends Annotation> annotationType) {
		return Throwable.class.isAssignableFrom(parameterType);
	}

	@Override
	public Throwable resolveArgument(final Class<?> argumentType, final Annotation annotation, final String name,
			final WebScriptRequest request, final WebScriptResponse response) {
		return ((AnnotationWebScriptRequest) request).getThrownException();
	}

}
