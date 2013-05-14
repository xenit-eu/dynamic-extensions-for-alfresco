package nl.runnable.alfresco.webscripts;

import javax.annotation.ManagedBean;

import nl.runnable.alfresco.webscripts.annotations.ExceptionHandler;
import nl.runnable.alfresco.webscripts.annotations.Uri;
import nl.runnable.alfresco.webscripts.annotations.WebScript;

@ManagedBean
@WebScript
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
