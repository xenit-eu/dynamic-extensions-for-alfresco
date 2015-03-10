package com.github.dynamicextensionsalfresco.event.events;

import com.github.dynamicextensionsalfresco.event.Event;
import org.osgi.framework.Bundle;

/**
 * @author Laurent Van der Linden
 */
public class SpringContextException implements Event {
	private Bundle bundle;
	private Exception exception;

	public SpringContextException(Bundle bundle, Exception exception) {
		this.bundle = bundle;
		this.exception = exception;
	}

	public Bundle getBundle() {
		return bundle;
	}

	public Exception getException() {
		return exception;
	}
}
