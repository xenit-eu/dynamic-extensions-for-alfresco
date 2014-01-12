package com.github.dynamicextensionsalfresco.webscripts;

import com.github.dynamicextensionsalfresco.webscripts.annotations.Attribute;
import com.github.dynamicextensionsalfresco.webscripts.arguments.HandlerMethodArgumentsResolver;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.extensions.webscripts.*;
import org.springframework.extensions.webscripts.Description.RequiredCache;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

public class AnnotationWebScript implements WebScript {

	private static final String URL_VARIABLE = "url";

	private static final String WEBSCRIPT_VARIABLE = "webscript";

	/* Dependencies */

	private HandlerMethodArgumentsResolver argumentsResolver;

	/* Configuration */

	private final Description description;

	private final Object handler;

	private final HandlerMethods handlerMethods;

	private final String id;

	/* Main operations */

	public AnnotationWebScript(final Description description, final Object handler,
			final HandlerMethods handlerMethods, final HandlerMethodArgumentsResolver argumentsResolver) {
		Assert.notNull(description, "Description cannot be null.");
		Assert.hasText(description.getId(), "No ID provided in Description.");
		Assert.notNull(handler, "Handler cannot be null.");
		Assert.notNull(handlerMethods, "Methods cannot be null.");

		this.description = description;
		this.handler = handler;
		this.handlerMethods = handlerMethods;
		this.argumentsResolver = argumentsResolver;
		this.id = description.getId();
	}

	public Object getHandler() {
		return handler;
	}

	public HandlerMethods getHandlerMethods() {
		return handlerMethods;
	}

	@Override
	public final void execute(final WebScriptRequest request, final WebScriptResponse response) throws IOException {
		final AnnotationWebScriptRequest annotationRequest = new AnnotationWebScriptRequest(request);
		final WebScriptResponseWrapper wrappedResponse = new WebScriptResponseWrapper(response);
		final Map<String, Object> model = new HashMap<String, Object>();
		try {
			invokeAttributeHandlerMethods(annotationRequest, wrappedResponse);
			invokeBeforeHandlerMethods(annotationRequest, wrappedResponse);
			final Object returnValue = invokeUriHandlerMethod(annotationRequest, wrappedResponse);
			handleUriMethodReturnValue(annotationRequest, wrappedResponse, returnValue);
		} catch (final Throwable e) {
			invokeExceptionHandlerMethods(e, annotationRequest, wrappedResponse, model);
		}
	}

	/*
	 * This method appears to be new in the Web Scripts 1.0.0 API. This implementation does nothing, because we want to
	 * retain backwards-compatibility.
	 */
	@Override
	public void init(final Container container, final Description description) {
	}

	@Override
	public Description getDescription() {
		return description;
	}

	@Override
	public ResourceBundle getResources() {
		/* Not yet supported. */
		return null;
	}

	@Override
	public void setURLModelFactory(final URLModelFactory arg0) {
		/* Not yet implemented. */
	}

	/* Handler operations */

	protected boolean invokeBeforeHandlerMethods(final AnnotationWebScriptRequest request,
			final WebScriptResponse response) {
		for (final Method method : handlerMethods.getBeforeMethods()) {
			method.setAccessible(true);
			final Object[] arguments = getArgumentsResolver().resolveHandlerMethodArguments(method, handler, request,
					response);
			final Object returnValue = ReflectionUtils.invokeMethod(method, handler, arguments);
			if (Boolean.FALSE.equals(returnValue)) {
				return false;
			}
		}
		return true;

	}

	protected void invokeAttributeHandlerMethods(final AnnotationWebScriptRequest request,
			final WebScriptResponse response) {
		for (final Method method : handlerMethods.getAttributeMethods()) {
			method.setAccessible(true);
			final Object[] arguments = getArgumentsResolver().resolveHandlerMethodArguments(method, handler, request,
					response);
			final Object attribute = ReflectionUtils.invokeMethod(method, handler, arguments);
			if (attribute == null) {
				continue;
			}
			final Attribute annotation = AnnotationUtils.findAnnotation(method, Attribute.class);
			final Map<String, Object> model = request.getModel();
			if (StringUtils.hasText(annotation.value())) {
				model.put(annotation.value(), attribute);
			} else {
				String name = method.getName();
				if (name.startsWith("get") && name.length() > 3) {
					name = name.substring(3, 4).toLowerCase() + name.substring(4);
				}
				model.put(name, attribute);
			}
		}
	}

	protected Object invokeUriHandlerMethod(final AnnotationWebScriptRequest request,
			final WebScriptResponseWrapper response) throws IOException {
		final Method uriMethod = handlerMethods.getUriMethod();
		final Object[] arguments = argumentsResolver.resolveHandlerMethodArguments(uriMethod, handler, request,
				response);
		uriMethod.setAccessible(true);
		return ReflectionUtils.invokeMethod(uriMethod, handler, arguments);
	}

	@SuppressWarnings("unchecked")
	protected void handleUriMethodReturnValue(final AnnotationWebScriptRequest request,
			final WebScriptResponseWrapper response, final Object returnValue) throws IOException {
		if (returnValue instanceof Map || handlerMethods.useResponseTemplate() || returnValue instanceof String) {
			final Map<String, Object> model = request.getModel();
			if (returnValue instanceof Map && returnValue != model) {
				model.putAll((Map<String, Object>) returnValue);
			}
			processHandlerMethodTemplate(request, model, response, response.getStatus(), returnValue);
		}
	}

	protected void invokeExceptionHandlerMethods(final Throwable exception, final AnnotationWebScriptRequest request,
			final WebScriptResponse response, final Map<String, Object> model) throws IOException {
		final List<Method> exceptionHandlerMethods = handlerMethods.findExceptionHandlers(exception);
		if (exceptionHandlerMethods.isEmpty()) {
			translateException(exception);
		}
		try {
			request.setThrownException(exception);
			for (final Method exceptionHandler : exceptionHandlerMethods) {
				final Object[] arguments = getArgumentsResolver().resolveHandlerMethodArguments(exceptionHandler,
						handler, request, response);
				exceptionHandler.setAccessible(true);
				ReflectionUtils.invokeMethod(exceptionHandler, handler, arguments);
			}
		} catch (final Throwable e) {
			translateException(e);
		} finally {
			request.setThrownException(null);
		}

	}

	/* Utility operations */

	protected void translateException(final Throwable e) throws IOException {
		if (e instanceof IOException) {
			throw (IOException) e;
		} else if (e instanceof RuntimeException) {
			throw (RuntimeException) e;
		} else {
			throw new RuntimeException(e);
		}
	}

	protected void processHandlerMethodTemplate(final WebScriptRequest request, final Map<String, Object> model,
			final WebScriptResponse response, Integer status, final Object returnValue) throws IOException {
		if (StringUtils.hasText(request.getFormat()) == false) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().write("No format specified.");
			return;
		}
		populateTemplateModel(model, request);
		status = status != null ? status : 200;
		processTemplate(request, model, status, response, returnValue);
	}

	/**
	 * Populates the model with utility objects for use in rendering templates.
	 * 
	 * @param model
	 * @param request
	 */
	protected void populateTemplateModel(final Map<String, Object> model, final WebScriptRequest request) {
		model.put(WEBSCRIPT_VARIABLE, description);
		model.put(URL_VARIABLE, new UrlModel(request));
	}

	protected void processTemplate(final WebScriptRequest request, final Map<String, Object> model, final int status,
			final WebScriptResponse response, final Object returnValue) throws IOException {
		final TemplateProcessorRegistry templateProcessorRegistry = request.getRuntime().getContainer()
				.getTemplateProcessorRegistry();
		final TemplateProcessor templateProcessor = templateProcessorRegistry.getTemplateProcessorByExtension("ftl");
		final String format = request.getFormat();
		String templateName = handlerMethods.getResponseTemplateName(returnValue);
		if (StringUtils.hasText(templateName) == false) {
			templateName = generateTemplateName(templateProcessor, format, status);
		}
		if (templateProcessor.hasTemplate(templateName)) {
			response.setContentType(Format.valueOf(format.toUpperCase()).mimetype());
			response.setContentEncoding("utf-8");
			addCacheControlHeaders(response);
			templateProcessor.process(templateName, model, response.getWriter());
		} else {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.getWriter().write(String.format("Could not find template: %s", templateName));
		}
	}

	protected String generateTemplateName(final TemplateProcessor templateProcessor, final String format,
			final int status) {
		final Class<?> handlerClass = AopUtils.getTargetClass(handler);
		final String methodName = handlerMethods.getUriMethod().getName();
		final String httpMethod = description.getMethod().toLowerCase();

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
		return templateName;
	}

	protected void addCacheControlHeaders(final WebScriptResponse response) {
		final List<String> cacheValues = new ArrayList<String>(3);
		final RequiredCache requiredCache = description.getRequiredCache();
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

	/* Dependencies */

	public void setArgumentsResolver(final HandlerMethodArgumentsResolver handlerMethodArgumentsResolver) {
		Assert.notNull(handlerMethodArgumentsResolver);
		this.argumentsResolver = handlerMethodArgumentsResolver;
	}

	protected HandlerMethodArgumentsResolver getArgumentsResolver() {
		return argumentsResolver;
	}

	/* Equality operations */

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final AnnotationWebScript other = (AnnotationWebScript) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

}
