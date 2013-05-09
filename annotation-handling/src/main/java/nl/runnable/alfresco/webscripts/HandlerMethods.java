package nl.runnable.alfresco.webscripts;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import nl.runnable.alfresco.webscripts.annotations.Attribute;
import nl.runnable.alfresco.webscripts.annotations.Before;
import nl.runnable.alfresco.webscripts.annotations.Uri;

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
