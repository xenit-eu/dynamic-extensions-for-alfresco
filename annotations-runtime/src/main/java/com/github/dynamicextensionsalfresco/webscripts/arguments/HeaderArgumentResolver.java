package com.github.dynamicextensionsalfresco.webscripts.arguments;

import java.lang.annotation.Annotation;

import com.github.dynamicextensionsalfresco.webscripts.annotations.Header;

import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * {@link ArgumentResolver} that maps String parameters annotated with {@link Header}.
 * 
 * @author Laurens Fridael
 * 
 */
public class HeaderArgumentResolver implements ArgumentResolver<String, Header> {

	@Override
	public boolean supports(final Class<?> argumentType, final Class<? extends Annotation> annotationType) {
		return String.class.equals(argumentType) && Header.class.equals(annotationType);
	}

	@Override
	public String resolveArgument(final Class<?> argumentType, final Header header, final String name,
			final WebScriptRequest request, final WebScriptResponse response) {
		return request.getHeader(header.value());
	}

}
