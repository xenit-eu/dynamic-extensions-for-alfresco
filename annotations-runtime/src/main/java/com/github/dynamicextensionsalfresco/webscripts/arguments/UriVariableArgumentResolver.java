package com.github.dynamicextensionsalfresco.webscripts.arguments;

import java.lang.annotation.Annotation;

import com.github.dynamicextensionsalfresco.webscripts.annotations.UriVariable;

import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class UriVariableArgumentResolver implements ArgumentResolver<Object, UriVariable> {

	/* Dependencies */

	private final StringValueConverter stringValueConverter;

	/* Main operations */

	UriVariableArgumentResolver(final StringValueConverter stringValueConverter) {
		Assert.notNull(stringValueConverter);
		this.stringValueConverter = stringValueConverter;
	}

	@Override
	public boolean supports(final Class<?> parameterType, final Class<? extends Annotation> annotationType) {
		return getStringValueConverter().isSupportedType(parameterType) && UriVariable.class.equals(annotationType);
	}

	@Override
	public Object resolveArgument(final Class<?> parameterType, final UriVariable uriVariable, final String name,
			final WebScriptRequest request, final WebScriptResponse response) {
		String variableName = uriVariable.value();
		if (StringUtils.hasText(variableName) == false) {
			variableName = name;
		}
		if (StringUtils.hasText(variableName) == false) {
			throw new RuntimeException(
					"Cannot determine name of URI variable. Specify the name using the @UriVariable annotation.");
		}
		final String variable = request.getServiceMatch().getTemplateVars().get(variableName);
		Object value = null;
		if (variable != null) {
			value = getStringValueConverter().convertStringValue(parameterType, variable);
		} else if (uriVariable.required()) {
			throw new IllegalStateException(String.format("URI variable not available: %s", variableName));
		}
		return value;

	}

	/* Dependencies */

	public StringValueConverter getStringValueConverter() {
		return stringValueConverter;
	}
}
