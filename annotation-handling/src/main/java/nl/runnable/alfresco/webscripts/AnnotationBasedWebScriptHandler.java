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
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import nl.runnable.alfresco.webscripts.annotations.Attribute;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.extensions.webscripts.DefaultURLModelFactory;
import org.springframework.extensions.webscripts.Format;
import org.springframework.extensions.webscripts.TemplateProcessor;
import org.springframework.extensions.webscripts.TemplateProcessorRegistry;
import org.springframework.extensions.webscripts.URLModelFactory;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/**
 * Handles {@link AnnotationBasedWebScript} requests.
 * 
 * @author Laurens Fridael
 * 
 */
public class AnnotationBasedWebScriptHandler {

	private static final String URL_VARIABLE = "url";

	private static final String WEBSCRIPT_VARIABLE = "webscript";

	/* Dependencies */

	private HandlerMethodArgumentsResolver handlerMethodArgumentsResolver = new DefaultHandlerMethodArgumentsResolver();

	private URLModelFactory urlModelFactory = new DefaultURLModelFactory();;

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
	public void handleRequest(final AnnotationBasedWebScript webScript, WebScriptRequest request,
			final WebScriptResponse response) throws IOException {
		Assert.notNull(webScript, "WebScript cannot be null.");
		Assert.notNull(request, "Request cannot be null.");
		Assert.notNull(response, "Response cannot be null.");

		final Map<String, Object> attributesByName = invokeAttributeMethods(webScript, request, response);
		request = new AttributesWebScriptRequest(request, attributesByName);
		invokeHandlerMethod(webScript, request, response);
	}

	/* Utility operations */

	protected Map<String, Object> invokeAttributeMethods(final AnnotationBasedWebScript webScript,
			final WebScriptRequest request, final WebScriptResponse response) {
		final Map<String, Object> attributesByName = new HashMap<String, Object>();
		for (final Method method : webScript.getAttributeMethods()) {
			method.setAccessible(true);
			final Object[] arguments = getHandlerMethodArgumentsResolver().resolveHandlerMethodArguments(method,
					webScript.getHandler(), request, response);
			final Object attribute = ReflectionUtils.invokeMethod(method, webScript.getHandler(), arguments);
			if (attribute == null) {
				continue;
			}
			final Attribute annotation = AnnotationUtils.findAnnotation(method, Attribute.class);
			if (StringUtils.hasText(annotation.value())) {
				attributesByName.put(annotation.value(), attribute);
			} else {
				String name = method.getName();
				if (name.startsWith("get") && name.length() > 3) {
					name = name.substring(3, 4).toLowerCase() + name.substring(4);
				}
				attributesByName.put(name, attribute);
			}
		}
		return attributesByName;
	}

	protected void invokeHandlerMethod(final AnnotationBasedWebScript webScript, final WebScriptRequest request,
			final WebScriptResponse response) throws IOException {
		final Object[] arguments = getHandlerMethodArgumentsResolver().resolveHandlerMethodArguments(
				webScript.getHandlerMethod(), webScript.getHandler(), request, response);
		final Object returnValue = ReflectionUtils.invokeMethod(webScript.getHandlerMethod(), webScript.getHandler(),
				arguments);
		processHandlerMethodReturnValue(webScript, returnValue, request, response);
	}

	@SuppressWarnings("unchecked")
	protected void processHandlerMethodReturnValue(final AnnotationBasedWebScript webScript, final Object returnValue,
			final WebScriptRequest request, final WebScriptResponse response) throws IOException {
		if (returnValue instanceof Map) {
			final Format format = Format.valueOf(request.getFormat().toUpperCase());
			if (format == null) {
				throw new IllegalStateException(String.format("Unknown format: %s", request.getFormat()));
			}
			final Map<String, Object> model = (Map<String, Object>) returnValue;
			final Class<?> handlerClass = webScript.getHandler().getClass();
			final String methodName = webScript.getHandlerMethod().getName();
			final String httpMethod = webScript.getDescription().getMethod().toLowerCase();
			final String baseTemplateName = String.format("%s.%s.%s", ClassUtils.getQualifiedName(handlerClass)
					.replace('.', '/'), methodName, httpMethod);
			final String templateName = String.format("%s.%s.ftl", baseTemplateName, request.getFormat().toLowerCase());
			populateTemplateModel(model, webScript, request);
			generateResponseFromTemplate(request, response, model, templateName);
		}
	}

	/**
	 * Populates the model with utility objects for use in rendering templates.
	 * 
	 * @param model
	 * @param request
	 */
	protected void populateTemplateModel(final Map<String, Object> model, final AnnotationBasedWebScript webScript,
			final WebScriptRequest request) {
		model.put(WEBSCRIPT_VARIABLE, webScript.getDescription());
		model.put(URL_VARIABLE, getUrlModelFactory().createURLModel(request));
	}

	protected void generateResponseFromTemplate(final WebScriptRequest request, final WebScriptResponse response,
			final Map<String, Object> model, final String templateName) throws IOException {
		final Writer out = response.getWriter();
		final TemplateProcessorRegistry templateProcessorRegistry = request.getRuntime().getContainer()
				.getTemplateProcessorRegistry();
		final TemplateProcessor templateProcessor = templateProcessorRegistry.getTemplateProcessorByExtension("ftl");
		if (templateProcessor.hasTemplate(templateName)) {
			final Format format = Format.valueOf(request.getFormat().toUpperCase());
			response.setContentType(format.mimetype());
			response.setContentEncoding("utf-8");
			templateProcessor.process(templateName, model, out);
		} else {
			// Friendly error message
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			out.write(String.format("Could not find template at path: %s", templateName));
		}
	}

	/* Dependencies */

	public void setHandlerMethodArgumentsResolver(final HandlerMethodArgumentsResolver handlerMethodArgumentsResolver) {
		Assert.notNull(handlerMethodArgumentsResolver);
		this.handlerMethodArgumentsResolver = handlerMethodArgumentsResolver;
	}

	protected HandlerMethodArgumentsResolver getHandlerMethodArgumentsResolver() {
		return handlerMethodArgumentsResolver;
	}

	public void setUrlModelFactory(final URLModelFactory urlModelFactory) {
		Assert.notNull(urlModelFactory);
		this.urlModelFactory = urlModelFactory;
	}

	protected URLModelFactory getUrlModelFactory() {
		return urlModelFactory;
	}

}
