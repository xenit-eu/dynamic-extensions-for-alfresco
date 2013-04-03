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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import nl.runnable.alfresco.webscripts.annotations.Attribute;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.extensions.webscripts.DefaultURLModelFactory;
import org.springframework.extensions.webscripts.Description.RequiredCache;
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

		final WebScriptResponseWrapper wrappedResponse = new WebScriptResponseWrapper(response);
		final Map<String, Object> attributesByName = invokeAttributeMethods(webScript, request, wrappedResponse);
		request = new AttributesWebScriptRequest(request, attributesByName);
		invokeHandlerMethod(webScript, request, wrappedResponse);
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
			final WebScriptResponseWrapper response) throws IOException {
		final Object[] arguments = getHandlerMethodArgumentsResolver().resolveHandlerMethodArguments(
				webScript.getHandlerMethod(), webScript.getHandler(), request, response);
		final Object returnValue = ReflectionUtils.invokeMethod(webScript.getHandlerMethod(), webScript.getHandler(),
				arguments);
		if (returnValue != null) {
			processHandlerMethodReturnValue(webScript, returnValue, request, response, response.getStatus());
		}
	}

	@SuppressWarnings("unchecked")
	protected void processHandlerMethodReturnValue(final AnnotationBasedWebScript webScript, final Object returnValue,
			final WebScriptRequest request, final WebScriptResponse response, Integer status) throws IOException {
		if (returnValue instanceof Map) {
			if (StringUtils.hasText(request.getFormat()) == false) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().write("No format specified.");
				return;
			}
			final Map<String, Object> model = (Map<String, Object>) returnValue;
			populateTemplateModel(model, webScript, request);
			status = status != null ? status : 200;
			processTemplate(webScript, request, status, model, response);
		}
	}

	protected void processTemplate(final AnnotationBasedWebScript webScript, final WebScriptRequest request,
			final int status, final Map<String, Object> model, final WebScriptResponse response) throws IOException {
		final String format = request.getFormat();
		final TemplateProcessorRegistry templateProcessorRegistry = request.getRuntime().getContainer()
				.getTemplateProcessorRegistry();
		final TemplateProcessor templateProcessor = templateProcessorRegistry.getTemplateProcessorByExtension("ftl");

		final Class<?> handlerClass = webScript.getHandler().getClass();
		final String methodName = webScript.getHandlerMethod().getName();
		final String httpMethod = webScript.getDescription().getMethod().toLowerCase();

		final String baseTemplateName = String.format("%s.%s.%s",
				ClassUtils.getQualifiedName(handlerClass).replace('.', '/'), methodName, httpMethod);
		/* <java class + method>.<http method>.<format>.ftl */
		final String defaultTemplateName = String.format("%s.%s.ftl", baseTemplateName, format.toLowerCase());

		/* <java class + method>.<http method>.<format>.<status>.ftl */
		String templateName = String.format("%s.%s.%d.ftl", baseTemplateName, format.toLowerCase(), status);
		if (templateProcessor.hasTemplate(templateName) == false) {
			final String packageName = handlerClass.getPackage().getName().replace('.', '/');
			/* <java package>.<format>.<status>.ftl */
			templateName = String.format("%s/%s.%d.ftl", packageName, format.toLowerCase(), status);
		}
		if (templateProcessor.hasTemplate(templateName) == false) {
			/* <format>.<status>.ftl */
			templateName = String.format("%s.%d.ftl", format, status);
		}
		if (templateProcessor.hasTemplate(templateName) == false) {
			templateName = defaultTemplateName;
		}
		if (templateProcessor.hasTemplate(templateName)) {
			response.setContentType(Format.valueOf(format.toUpperCase()).mimetype());
			response.setContentEncoding("utf-8");
			addCacheControlHeaders(webScript, response);
			templateProcessor.process(templateName, model, response.getWriter());
		} else {
			// Friendly error message
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.getWriter().write(String.format("Could not find template: %s", defaultTemplateName));
		}
	}

	protected void addCacheControlHeaders(final AnnotationBasedWebScript webScript, final WebScriptResponse response) {
		final List<String> cacheValues = new ArrayList<String>(3);
		final RequiredCache requiredCache = webScript.getDescription().getRequiredCache();
		if (requiredCache != null) {
			if (requiredCache.getNeverCache()) {
				cacheValues.add("no-cache");
				cacheValues.add("no-store");
			}
			if (requiredCache.getMustRevalidate()) {
				cacheValues.add("must-revalidate");
			}
		}
		if (cacheValues.isEmpty() == false) {
			response.setHeader("Cache-Control", StringUtils.collectionToDelimitedString(cacheValues, ", "));
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
			final Map<String, Object> model, final TemplateProcessor templateProcessor, final String templateName,
			final String format) throws IOException {
		final Writer out = response.getWriter();
		if (templateProcessor.hasTemplate(templateName)) {
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
