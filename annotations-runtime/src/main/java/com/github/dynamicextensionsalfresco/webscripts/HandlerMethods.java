package com.github.dynamicextensionsalfresco.webscripts;

import com.github.dynamicextensionsalfresco.webscripts.annotations.Attribute;
import com.github.dynamicextensionsalfresco.webscripts.annotations.Before;
import com.github.dynamicextensionsalfresco.webscripts.annotations.ResponseTemplate;
import com.github.dynamicextensionsalfresco.webscripts.annotations.Uri;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Parameter object for specifying {@link Uri}, {@link Before} and {@link Attribute}-annotated Web Script handler
 * methods.
 * 
 * @author Laurens Fridael
 * 
 */
public class HandlerMethods {

	private final List<Method> beforeMethods = new ArrayList<Method>();

	private final List<Method> attributeMethods = new ArrayList<Method>();

	private Method uriMethod;

	private final List<ExceptionHandlerMethod> exceptionHandlerMethods = new ArrayList<ExceptionHandlerMethod>();

	public List<Method> getBeforeMethods() {
		return beforeMethods;
	}

	public List<Method> getAttributeMethods() {
		return attributeMethods;
	}

	public Method getUriMethod() {
		return uriMethod;
	}

	public List<ExceptionHandlerMethod> getExceptionHandlerMethods() {
		return exceptionHandlerMethods;
	}

	public List<Method> findExceptionHandlers(final Throwable exception) {
		final List<Method> handlerMethods = new ArrayList<Method>(1);
		for (final ExceptionHandlerMethod exceptionHandlerMethod : getExceptionHandlerMethods()) {
			if (exceptionHandlerMethod.canHandle(exception)) {
				handlerMethods.add(exceptionHandlerMethod.getMethod());
			}
		}
		return handlerMethods;
	}

	public boolean useResponseTemplate() {
		return (AnnotationUtils.findAnnotation(uriMethod, ResponseTemplate.class) != null);
	}

	public String getResponseTemplateName() {
		final ResponseTemplate responseTemplate = AnnotationUtils.findAnnotation(uriMethod, ResponseTemplate.class);
		return (responseTemplate != null ? responseTemplate.value() : null);
	}

	/**
	 * Creates a new instance for the specified {@link Uri}-annotated method.
	 * 
	 * @param uriMethod
	 */
	public HandlerMethods createForUriMethod(final Method uriMethod) {
		final HandlerMethods handlerMethods = new HandlerMethods();
		handlerMethods.beforeMethods.addAll(getBeforeMethods());
		handlerMethods.attributeMethods.addAll(getAttributeMethods());
		handlerMethods.uriMethod = uriMethod;
		handlerMethods.exceptionHandlerMethods.addAll(getExceptionHandlerMethods());
		return handlerMethods;
	}

}
