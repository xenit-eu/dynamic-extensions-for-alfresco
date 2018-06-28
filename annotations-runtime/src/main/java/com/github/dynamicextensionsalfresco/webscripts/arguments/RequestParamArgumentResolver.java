package com.github.dynamicextensionsalfresco.webscripts.arguments;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;

import com.github.dynamicextensionsalfresco.webscripts.annotations.RequestParam;

import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * {@link ArgumentResolver} that handles parameters annotated with {@link RequestParam}.
 * 
 * @author Laurens Fridael
 */
public class RequestParamArgumentResolver implements ArgumentResolver<Object, RequestParam> {

	/* Dependencies */

	private final StringValueConverter stringValueConverter;

	/* Main operations */

	RequestParamArgumentResolver(final StringValueConverter stringValueConverter) {
		Assert.notNull(stringValueConverter);
		this.stringValueConverter = stringValueConverter;
	}

	@Override
	public boolean supports(Class<?> parameterType, final Class<? extends Annotation> annotationType) {
		if (parameterType.isArray()) {
			parameterType = parameterType.getComponentType();
		}
		return getStringValueConverter().isSupportedType(parameterType) && RequestParam.class.equals(annotationType);
	}

	@Override
	public Object resolveArgument(final Class<?> parameterType, final RequestParam requestParam, final String name,
			final WebScriptRequest request, final WebScriptResponse response) {
		Assert.notNull(requestParam, "RequestParam annotation cannot be null.");
		String parameterName = requestParam.value();
		if (StringUtils.hasText(parameterName) == false) {
			parameterName = name;
		}
		if (StringUtils.hasText(parameterName) == false) {
			throw new RuntimeException(
					"Cannot determine name of request parameter. Specify the name using the @RequestParam annotation.");
		}
		final Object parameter;
		if (parameterType.isArray() == false) {
			parameter = handleSingleParameter(parameterType, requestParam, request, parameterName);
		} else if (StringUtils.hasText(requestParam.delimiter())) {
			parameter = handleDelimitedParameter(parameterType, requestParam, request, parameterName);
		} else {
			parameter = handleMultipleParameters(parameterType, requestParam, request, parameterName);

		}
		return parameter;
	}

	/* Utility operations */

	private Object handleSingleParameter(final Class<?> parameterType, final RequestParam requestParam,
			final WebScriptRequest request, final String parameterName) {
		final String parameterValue = request.getParameter(parameterName);
		Object value = null;
		if (parameterValue != null) {
			value = getStringValueConverter().convertStringValue(parameterType, parameterValue);
		} else {
			if (StringUtils.hasText(requestParam.defaultValue())) {
				value = getStringValueConverter().convertStringValue(parameterType, requestParam.defaultValue());
			}
			if (requestParam.required() && value == null) {
				throw new IllegalStateException(String.format("Request parameter not available: %s", parameterName));
			}
		}
		return value;
	}

	private Object handleDelimitedParameter(final Class<?> parameterType, final RequestParam requestParam,
			final WebScriptRequest request, final String parameterName) {
		String parameterValue = request.getParameter(parameterName);
		if (parameterValue == null) {
			if (StringUtils.hasText(requestParam.defaultValue())) {
				parameterValue = requestParam.defaultValue();
			} else if (requestParam.required()) {
				throw new IllegalStateException(String.format("Request parameter not available: %s", parameterName));
			}
		}
        if (parameterValue != null) {
            final String[] parameterValues = parameterValue.split(requestParam.delimiter());
            return convertToArray(parameterType.getComponentType(), parameterValues);
        } else {
            return null;
        }
    }

	private Object handleMultipleParameters(final Class<?> parameterType, final RequestParam requestParam,
			final WebScriptRequest request, final String parameterName) {
		String[] parameterValues = request.getParameterValues(parameterName);
		if (parameterValues == null) {
			if (requestParam.required()) {
				throw new IllegalStateException(String.format("Request parameter not available: %s", parameterName));
			} else {
				parameterValues = new String[] { requestParam.defaultValue() };
			}
		}
		return convertToArray(parameterType.getComponentType(), parameterValues);
	}

	private Object[] convertToArray(final Class<?> arrayComponentType, final String[] parameterValues) {
		final Object[] values = (Object[]) Array.newInstance(arrayComponentType, parameterValues.length);
		for (int i = 0; i < parameterValues.length; i++) {
			values[i] = getStringValueConverter().convertStringValue(arrayComponentType, parameterValues[i]);
		}
		return values;
	}

	/* Dependencies */

	protected StringValueConverter getStringValueConverter() {
		return stringValueConverter;
	}

}
