package nl.runnable.alfresco.webscripts;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.extensions.webscripts.Container;
import org.springframework.extensions.webscripts.Description;
import org.springframework.extensions.webscripts.DescriptionImpl;
import org.springframework.extensions.webscripts.URLModelFactory;
import org.springframework.extensions.webscripts.WebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.util.Assert;

class AnnotationBasedWebScript implements WebScript {

	private final Description description;

	private final String id;

	private final Object handler;

	private final Method handlerMethod;

	private final List<Method> attributeMethods;

	private final AnnotationBasedWebScriptHandler annotationMethodHandler;

	public AnnotationBasedWebScript(final DescriptionImpl description, final Object handler,
			final Method handlerMethod, final List<Method> attributeMethods,
			final AnnotationBasedWebScriptHandler annotationMethodHandler) {
		Assert.notNull(description, "Description cannot be null.");
		Assert.hasText(description.getId(), "No ID provided in Description.");
		Assert.notNull(handler, "Handler cannot be null.");
		Assert.notNull(handlerMethod, "Method cannot be null.");
		Assert.notNull(attributeMethods);
		Assert.notNull(annotationMethodHandler);

		this.description = description;
		this.handler = handler;
		this.handlerMethod = handlerMethod;
		this.attributeMethods = attributeMethods;
		this.annotationMethodHandler = annotationMethodHandler;
		this.id = description.getId();
	}

	public Object getHandler() {
		return handler;
	}

	public Method getHandlerMethod() {
		return handlerMethod;
	}

	public List<Method> getAttributeMethods() {
		return attributeMethods;
	}

	@Override
	public final void execute(final WebScriptRequest request, final WebScriptResponse response) throws IOException {
		annotationMethodHandler.handleRequest(this, request, response);
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
