package nl.runnable.alfresco.webscripts;

import java.io.IOException;
import java.util.ResourceBundle;

import org.springframework.extensions.webscripts.Container;
import org.springframework.extensions.webscripts.Description;
import org.springframework.extensions.webscripts.DescriptionImpl;
import org.springframework.extensions.webscripts.URLModelFactory;
import org.springframework.extensions.webscripts.WebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.util.Assert;

public class AnnotationBasedWebScript implements WebScript {

	private final Description description;

	private final String id;

	private final Object handler;

	private final HandlerMethods handlerMethods;

	private final AnnotationBasedWebScriptHandler webScriptHandler;

	public AnnotationBasedWebScript(final AnnotationBasedWebScriptHandler webScriptHandler,
			final DescriptionImpl description, final Object handler, final HandlerMethods handlerMethods) {
		Assert.notNull(description, "Description cannot be null.");
		Assert.hasText(description.getId(), "No ID provided in Description.");
		Assert.notNull(handler, "Handler cannot be null.");
		Assert.notNull(handlerMethods, "Methods cannot be null.");
		Assert.notNull(webScriptHandler);

		this.description = description;
		this.handler = handler;
		this.handlerMethods = handlerMethods;
		this.id = description.getId();
		this.webScriptHandler = webScriptHandler;
	}

	public Object getHandler() {
		return handler;
	}

	public HandlerMethods getHandlerMethods() {
		return handlerMethods;
	}

	@Override
	public final void execute(final WebScriptRequest request, final WebScriptResponse response) throws IOException {
		webScriptHandler.handleRequest(this, request, response);
	}

	/*
	 * This method appears to be new in the Web Scripts 1.0.0 API. This implementation does nothing, because we want to
	 * retain backwards-compatibility.
	 */
	@Override
	public void init(final Container container, final Description description) {
	}

	@Override
	public Description getDescription() {
		return description;
	}

	@Override
	public ResourceBundle getResources() {
		/* Not yet supported. */
		return null;
	}

	@Override
	public void setURLModelFactory(final URLModelFactory arg0) {
		/* Not yet implemented. */
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final AnnotationBasedWebScript other = (AnnotationBasedWebScript) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

}
