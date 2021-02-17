package com.github.dynamicextensionsalfresco.webscripts;

import com.github.dynamicextensionsalfresco.spring.Spied;
import com.github.dynamicextensionsalfresco.webscripts.annotations.ExceptionHandler;
import com.github.dynamicextensionsalfresco.webscripts.annotations.Uri;

import org.springframework.stereotype.Component;

@Component
@Spied
public class ExceptionHandlerExample extends ExceptionHandlerAbstractClass implements ExceptionHandlerInterface {

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

	@Uri("/throwUnsupportedOperationException")
	public void throwUnsupportedOperationException() {
		throw new UnsupportedOperationException();
	}

	@Uri("/throwIndexOutOfBoundsException")
	public void throwIndexOutOfBoundsException() {
		throw new IndexOutOfBoundsException();
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
