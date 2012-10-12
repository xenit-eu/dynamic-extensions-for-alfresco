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
import java.util.HashSet;
import java.util.Set;

import nl.runnable.alfresco.webscripts.annotations.RequestParam;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * {@link ArgumentResolver} that handles parameters annotated with {@link RequestParam}.
 * 
 * @author Laurens Fridael
 * 
 */
public class RequestParameterArgumentResolver implements ArgumentResolver<Object, RequestParam> {

	private static final Set<Class<?>> SUPPORTED_TYPES = new HashSet<Class<?>>();
	static {
		SUPPORTED_TYPES.add(String.class);
		SUPPORTED_TYPES.add(String[].class);
		SUPPORTED_TYPES.add(Integer.TYPE);
		SUPPORTED_TYPES.add(int[].class);
		SUPPORTED_TYPES.add(Integer.class);
		SUPPORTED_TYPES.add(Integer[].class);
		SUPPORTED_TYPES.add(Boolean.TYPE);
		SUPPORTED_TYPES.add(boolean[].class);
		SUPPORTED_TYPES.add(Boolean.class);
		SUPPORTED_TYPES.add(Boolean[].class);
		SUPPORTED_TYPES.add(QName.class);
		SUPPORTED_TYPES.add(NodeRef.class);
	}

	@Override
	public boolean supports(final Class<?> parameterType, final Class<? extends Annotation> annotationType) {
		return SUPPORTED_TYPES.contains(parameterType) && RequestParam.class.equals(annotationType);
	}

	@Override
	public Object resolveArgument(final Class<?> parameterType, final RequestParam requestParameter,
			final String name, final WebScriptRequest request, final WebScriptResponse response) {
		Assert.notNull(requestParameter, "RequestParam annotation cannot be null.");
		String parameterName = requestParameter.value();
		if (StringUtils.hasText(parameterName) == false) {
			parameterName = name;
		}
		if (StringUtils.hasText(parameterName) == false) {
			throw new RuntimeException(
					"Cannot determine name of request parameter. Specify the name using the @RequestParam annotation.");
		}
		final Object parameter;
		if (parameterType.isArray() == false) {
			String parameterValue = request.getParameter(parameterName);
			if (parameterValue == null) {
				if (StringUtils.hasText(requestParameter.defaultValue())) {
					parameterValue = requestParameter.defaultValue();
				} else if (requestParameter.required()) {
					throw new IllegalStateException(String.format("Request parameter not available: %s", parameterName));
				}
			}
			parameter = convertStringValue(parameterType, parameterValue);
		} else {
			String[] parameterValues = request.getParameterValues(parameterName);
			if (parameterValues == null) {
				if (requestParameter.required()) {
					throw new IllegalStateException(String.format("Request parameter not available: %s", parameterName));
				} else {
					parameterValues = new String[] { requestParameter.defaultValue() };
				}
			}
			final Object[] values = new Object[parameterValues.length];
			for (int i = 0; i < parameterValues.length; i++) {
				values[i] = convertStringValue(parameterType.getComponentType(), parameterValues[i]);
			}
			parameter = values;

		}
		return parameter;
	}

	@SuppressWarnings("unchecked")
	private static <T> T convertStringValue(final Class<T> type, final String stringValue) {
		Object value;
		if (String.class.equals(type)) {
			value = stringValue;
		} else if (Integer.TYPE.equals(type)) {
			value = Integer.parseInt(stringValue);
		} else if (Integer.class.equals(type)) {
			value = Integer.valueOf(stringValue);
		} else if (Boolean.TYPE.equals(type)) {
			value = Boolean.parseBoolean(stringValue);
		} else if (Boolean.class.equals(type)) {
			value = Boolean.valueOf(stringValue);
		} else if (QName.class.equals(type)) {
			if (stringValue.matches("\\{\\.+?\\}\\.+?")) {
				value = QName.createQName(stringValue);
			} else if (stringValue.matches("\\.+?:\\.+?")) {
				throw new IllegalArgumentException("Specifying QNames in prefix format is not yet supported: "
						+ stringValue);
				// TODO: Find a way to obtain a NamespacePrefixResolver.
				// value = QName.createQName(stringValue, namespacePrefixResolver);
			} else {
				throw new IllegalArgumentException("Invalid QName format: " + stringValue);
			}
		} else if (NodeRef.class.equals(type)) {
			value = new NodeRef(stringValue);
		} else {
			throw new IllegalArgumentException(String.format("Unhandled parameter type %s", type));
		}
		return (T) value;
	}

}
