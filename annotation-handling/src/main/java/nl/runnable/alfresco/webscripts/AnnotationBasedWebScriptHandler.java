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

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import nl.runnable.alfresco.webscripts.annotations.ReferenceData;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/**
 * Handles {@link AnnotationBasedWebScript} requests.
 * 
 * @author Laurens Fridael
 * 
 */
public class AnnotationBasedWebScriptHandler {

	/* Dependencies */

	private HandlerMethodArgumentsResolver handlerMethodArgumentsResolver = new DefaultHandlerMethodArgumentsResolver();

	/* Main Operations */

	/**
	 * Handles {@link AnnotationBasedWebScript} requests.
	 * 
	 * @param request
	 * @param response
	 * @param handler
	 * @param handlerMethod
	 * @throws IOException
	 */
	public void handleRequest(final AnnotationBasedWebScript webScript, final WebScriptRequest request,
			final WebScriptResponse response) throws IOException {
		Assert.notNull(webScript, "WebScript cannot be null.");
		Assert.notNull(request, "Request cannot be null.");
		Assert.notNull(response, "Response cannot be null.");

		final Map<String, Object> referenceDataByName = invokeReferenceDataMethods(webScript, request, response);
		invokeHandlerMethod(webScript, request, response, referenceDataByName);
	}

	/* Utility operations */

	protected Map<String, Object> invokeReferenceDataMethods(final AnnotationBasedWebScript webScript,
			final WebScriptRequest request, final WebScriptResponse response) {

		final Map<String, Object> referenceDataByName = new HashMap<String, Object>();
		for (final Method method : webScript.getReferenceDataMethods()) {
			method.setAccessible(true);
			final Object[] arguments = getHandlerMethodArgumentsResolver().resolveHandlerMethodArguments(method,
					webScript.getHandler(), request, response);
			final Object referenceData = ReflectionUtils.invokeMethod(method, webScript.getHandler(), arguments);
			final ReferenceData annotation = AnnotationUtils.findAnnotation(method, ReferenceData.class);
			if (StringUtils.hasText(annotation.value())) {
				referenceDataByName.put(annotation.value(), referenceData);
			} else {
				String name = method.getName();
				if (name.startsWith("get") && name.length() > 3) {
					name = name.substring(3, 4).toLowerCase() + name.substring(4);
				}
				referenceDataByName.put(name, referenceData);
			}

		}
		return referenceDataByName;
	}

	protected void invokeHandlerMethod(final AnnotationBasedWebScript webScript, final WebScriptRequest request,
			final WebScriptResponse response, final Map<String, Object> referenceDataByName) throws IOException {
		ReferenceDataArgumentResolver.setCurrentReferenceData(referenceDataByName);
		try {
			final Object[] arguments = getHandlerMethodArgumentsResolver().resolveHandlerMethodArguments(
					webScript.getHandlerMethod(), webScript.getHandler(), request, response);
			final Object returnValue = ReflectionUtils.invokeMethod(webScript.getHandlerMethod(),
					webScript.getHandler(), arguments);
			processHandlerMethodReturnValue(returnValue, request, response);
		} finally {
			ReferenceDataArgumentResolver.clearCurrentReferenceData();
		}
	}

	/**
	 * Processes a given return value from a handler method of an annotation-based WebScript.
	 * <p>
	 * This implementation does nothing, but subclasses may override this.
	 * 
	 * @param returnValue
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	protected void processHandlerMethodReturnValue(final Object returnValue, final WebScriptRequest request,
			final WebScriptResponse response) throws IOException {
	}

	/* Dependencies */

	public void setHandlerMethodArgumentsResolver(final HandlerMethodArgumentsResolver handlerMethodArgumentsResolver) {
		Assert.notNull(handlerMethodArgumentsResolver);
		this.handlerMethodArgumentsResolver = handlerMethodArgumentsResolver;
	}

	protected HandlerMethodArgumentsResolver getHandlerMethodArgumentsResolver() {
		return handlerMethodArgumentsResolver;
	}

}
