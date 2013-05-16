package nl.runnable.alfresco.webscripts;

import java.lang.reflect.Method;

import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * Strategy for resolving handler method arguments.
 * 
 * @author Laurens Fridael
 * @see ArgumentResolver
 */
public interface HandlerMethodArgumentsResolver {

	/**
	 * Resolves the arguments of the given handler method.
	 * 
	 * @param handlerMethod
	 *            The handler method.
	 * @param handler
	 *            The handler itself.
	 * @param request
	 * @param response
	 * @return The arguments to invoke handlerMethod with.
	 */
	public Object[] resolveHandlerMethodArguments(final Method handlerMethod, final Object handler,
			final WebScriptRequest request, final WebScriptResponse response);

}
