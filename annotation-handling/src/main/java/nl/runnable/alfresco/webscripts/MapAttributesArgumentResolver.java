package nl.runnable.alfresco.webscripts;

import java.lang.annotation.Annotation;
import java.util.Map;

import nl.runnable.alfresco.webscripts.annotations.Attribute;

import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * Resolves {@link Map} handler method arguments to the attributes provided by {@link Attribute} methods.
 * 
 * @author Laurens Fridael
 * 
 */
public class MapAttributesArgumentResolver implements ArgumentResolver<Map<String, Object>, Annotation> {

	@Override
	public boolean supports(final Class<?> parameterType, final Class<? extends Annotation> annotationType) {
		return Map.class.isAssignableFrom(parameterType);
	}

	@Override
	public Map<String, Object> resolveArgument(final Class<?> argumentType, final Annotation parameterAnnotation,
			final String name, final WebScriptRequest request, final WebScriptResponse response) {
		return ((AnnotationBasedWebScriptRequest) request).getModel();
	}

}
