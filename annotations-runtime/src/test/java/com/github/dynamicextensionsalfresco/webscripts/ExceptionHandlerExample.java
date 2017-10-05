package com.github.dynamicextensionsalfresco.webscripts;

import com.github.dynamicextensionsalfresco.spring.Spied;
import com.github.dynamicextensionsalfresco.webscripts.annotations.ExceptionHandler;
import com.github.dynamicextensionsalfresco.webscripts.annotations.Uri;
import org.springframework.stereotype.Component;

@Component
@Spied
public class ExceptionHandlerExample {

	IllegalArgumentException illegalArgumentException;

	IllegalStateException illegalStateException;

	Throwable throwable;

	/* Main operations */

	@Uri("/throwIllegalArgumentException")
	public void throwIllegalArgumentException() {
		throw new IllegalArgumentException();
	}

	@Uri("/throwIllegalStateException")
	public void throwIllegalStateException() {
		throw new IllegalStateException();
	}

	/* Utility operations */

	@ExceptionHandler(IllegalArgumentException.class)
	protected void handleIllegalArgument(final IllegalArgumentException exception) {
		this.illegalArgumentException = exception;
	}

	@ExceptionHandler(IllegalStateException.class)
	protected void handleIllegalStateException(final IllegalStateException exception) {
		this.illegalStateException = exception;
	}

	@ExceptionHandler
	protected void handleThrowable(final Throwable exception) {
		this.throwable = exception;
	}

}
