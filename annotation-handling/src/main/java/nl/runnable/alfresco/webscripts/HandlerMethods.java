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

	public List<Method> getBeforeMethods() {
		return beforeMethods;
	}

	public List<Method> getAttributeMethods() {
		return attributeMethods;
	}

	public Method getUriMethod() {
		return uriMethod;
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
		return handlerMethods;
	}

}
