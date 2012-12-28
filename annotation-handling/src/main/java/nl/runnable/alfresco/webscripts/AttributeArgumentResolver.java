package nl.runnable.alfresco.webscripts;

import java.lang.annotation.Annotation;
import java.util.Map;

import nl.runnable.alfresco.webscripts.annotations.Attribute;

import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.util.StringUtils;

public class AttributeArgumentResolver implements ArgumentResolver<Object, Attribute> {

	/**
	 * Currently we use thread-local storage for exchanging attributes between {@link AnnotationBasedWebScriptHandler}
	 * and this class. It's an ugly solution; one that may be refactored later on.
	 * 
	 */
	/* Rename to 'currentAttributes'. */
	static ThreadLocal<Map<String, Object>> currentAttributes = new ThreadLocal<Map<String, Object>>();

	static void setCurrentAttributes(final Map<String, Object> currentAttributesByName) {
		currentAttributes.set(currentAttributesByName);
	}

	static void clearCurrentAttributes() {
		currentAttributes.remove();
	}

	@Override
	public boolean supports(final Class<?> parameterType, final Class<? extends Annotation> annotationType) {
		return Attribute.class.equals(annotationType);
	}

	@Override
	public Object resolveArgument(final Class<?> argumentType, final Attribute attribute, String name,
			final WebScriptRequest request, final WebScriptResponse response) {
		Object value = null;
		final Map<String, Object> attributesByName = currentAttributes.get();
		if (StringUtils.hasText(attribute.value())) {
			name = attribute.value();
		}
		if (attributesByName.containsKey(name)) {
			value = attributesByName.get(name);
		} else {
			value = resolveByType(argumentType, attributesByName);
		}
		if (value == null && attribute.required()) {
			throw new RuntimeException("Cannot find reference data for method argument '" + name + "'.");
		}
		return value;
	}

	/* Utility operations */

	protected Object resolveByType(final Class<?> argumentType, final Map<String, Object> attributesByName) {
		Object value = null;
		for (final Object attributeValue : attributesByName.values()) {
			if (argumentType.isInstance(attributeValue)) {
				value = attributeValue;
				break;
			}
		}
		return value;
	}

}
