package nl.runnable.alfresco.webscripts;

import java.lang.annotation.Annotation;
import java.util.Map;

import nl.runnable.alfresco.webscripts.annotations.ReferenceData;

import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.util.StringUtils;

public class ReferenceDataArgumentResolver implements ArgumentResolver<Object, ReferenceData> {

	/**
	 * Currently we use thread-local storage for exchanging reference data between
	 * {@link AnnotationBasedWebScriptHandler} and this class. It's an ugly solution; one that may be refactored later
	 * on.
	 * 
	 */
	static ThreadLocal<Map<String, Object>> currentReferenceData = new ThreadLocal<Map<String, Object>>();

	static void setCurrentReferenceData(final Map<String, Object> referenceDataByName) {
		currentReferenceData.set(referenceDataByName);
	}

	static void clearCurrentReferenceData() {
		currentReferenceData.remove();
	}

	@Override
	public boolean supports(final Class<?> parameterType, final Class<? extends Annotation> annotationType) {
		return ReferenceData.class.equals(annotationType);
	}

	@Override
	public Object resolveArgument(final Class<?> argumentType, final ReferenceData referenceData, String name,
			final WebScriptRequest request, final WebScriptResponse response) {
		Object value = null;
		final Map<String, Object> referenceDataByName = currentReferenceData.get();
		if (StringUtils.hasText(referenceData.value())) {
			name = referenceData.value();
		}
		if (referenceDataByName.containsKey(name)) {
			value = referenceDataByName.get(name);
		} else {
			value = resolveByType(argumentType);
		}
		if (value == null && referenceData.required()) {
			throw new RuntimeException("Cannot find reference data for method argument '" + name + "'.");
		}
		return value;
	}

	/* Utility operations */

	protected Object resolveByType(final Class<?> argumentType) {
		Object value = null;
		for (final Object referenceDataValue : currentReferenceData.get().values()) {
			if (argumentType.isInstance(referenceDataValue)) {
				value = referenceDataValue;
				break;
			}
		}
		return value;
	}

}
