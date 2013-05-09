package nl.runnable.alfresco.webscripts;

import java.lang.reflect.Method;

class ExceptionHandlerMethod {

	private final Class<? extends Throwable>[] exceptionTypes;

	private final Method method;

	ExceptionHandlerMethod(final Class<? extends Throwable>[] exceptionTypes, final Method method) {
		this.exceptionTypes = exceptionTypes;
		this.method = method;
	}

	public boolean canHandle(final Throwable exception) {
		for (final Class<? extends Throwable> type : exceptionTypes) {
			if (type.isInstance(exception)) {
				return true;
			}
		}
		return false;
	}

	public Method getMethod() {
		return method;
	}

}
