package nl.runnable.alfresco.webscripts;

import javax.annotation.ManagedBean;

import nl.runnable.alfresco.webscripts.annotations.ExceptionHandler;
import nl.runnable.alfresco.webscripts.annotations.Uri;
import nl.runnable.alfresco.webscripts.annotations.WebScript;

@ManagedBean
@WebScript
public class ExceptionHandlerExample {

	IllegalArgumentException illegalArgumentException;

	@Uri("/throwIllegalArgumentException")
	public void throwIllegalArgumentException() {
		throw new IllegalArgumentException();
	}

	@ExceptionHandler(IllegalArgumentException.class)
	protected void handleIllegalArgument(final IllegalArgumentException exception) {
		this.illegalArgumentException = exception;
	}
}
