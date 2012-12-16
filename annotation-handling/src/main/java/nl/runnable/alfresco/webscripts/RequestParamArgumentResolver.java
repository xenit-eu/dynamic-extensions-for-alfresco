/*
Copyright (c) 2012, Runnable
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
 * Neither the name of Runnable nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package nl.runnable.alfresco.webscripts;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;

import nl.runnable.alfresco.webscripts.annotations.RequestParam;

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
		String parameterValue = request.getParameter(parameterName);
		if (parameterValue == null) {
			if (StringUtils.hasText(requestParam.defaultValue())) {
				parameterValue = requestParam.defaultValue();
			} else if (requestParam.required()) {
				throw new IllegalStateException(String.format("Request parameter not available: %s", parameterName));
			}
		}
		return getStringValueConverter().convertStringValue(parameterType, parameterValue);
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
		final String[] parameterValues = parameterValue.split(requestParam.delimiter());
		return convertToArray(parameterType.getComponentType(), parameterValues);
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
