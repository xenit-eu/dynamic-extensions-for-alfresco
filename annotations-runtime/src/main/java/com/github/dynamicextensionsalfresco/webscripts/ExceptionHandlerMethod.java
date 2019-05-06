package com.github.dynamicextensionsalfresco.webscripts;

import com.github.dynamicextensionsalfresco.webscripts.annotations.ExceptionHandler;

import java.lang.reflect.Method;

class ExceptionHandlerMethod {

	private final Class<? extends Throwable>[] exceptionTypes;

	private final Method method;

	ExceptionHandlerMethod(final ExceptionHandler exceptionHandlerAnnotation, final Method method) {
		this.exceptionTypes = exceptionHandlerAnnotation.value();
		this.method = method;
	}

	public boolean canHandle(final Throwable exception) {
		if (exceptionTypes.length == 0) {
			return true;
		} else {
			for (final Class<? extends Throwable> type : exceptionTypes) {
				if (type.isInstance(exception)) {
					return true;
				}
			}
			return false;
		}
	}

	public Method getMethod() {
		return method;
	}

}
