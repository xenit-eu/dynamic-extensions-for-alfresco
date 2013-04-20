package nl.runnable.alfresco.webscripts;

import java.lang.annotation.Annotation;

import nl.runnable.alfresco.webscripts.annotations.UriVariable;

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
		if (variable == null && uriVariable.required()) {
			throw new IllegalStateException(String.format("URI variable not available: %s", variableName));
		}
		return getStringValueConverter().convertStringValue(parameterType, variable);
	}

	/* Dependencies */

	public StringValueConverter getStringValueConverter() {
		return stringValueConverter;
	}
}
