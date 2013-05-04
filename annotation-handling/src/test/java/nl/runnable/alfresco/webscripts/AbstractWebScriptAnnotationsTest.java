package nl.runnable.alfresco.webscripts;

import static org.junit.Assert.*;

import java.io.IOException;

import nl.runnable.alfresco.webscripts.annotations.HttpMethod;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.extensions.webscripts.Registry;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.util.ClassUtils;

public abstract class AbstractWebScriptAnnotationsTest {

	/* Dependencies */

	@Autowired
	private Registry registry;

	/* Utility operations */

	protected void handleRequest(final HttpMethod httpMethod, final Object mockedHandler, final String methodName,
			final WebScriptRequest request, final WebScriptResponse response) {
		try {
			// Mockito extends the original object's class.
			final Class<?> actualClass = mockedHandler.getClass().getSuperclass();
			final AnnotationBasedWebScript webScript = webScriptFor(httpMethod, actualClass, methodName);
			webScript.execute(request, response);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected void handleGetRequest(final Object mockedHandler, final String methodName, final WebScriptRequest request,
			final WebScriptResponse response) {
		handleRequest(HttpMethod.GET, mockedHandler, methodName, request, response);
	}

	protected void handleGetRequest(final Object mockedHandler, final String methodName, final WebScriptRequest request) {
		handleRequest(HttpMethod.GET, mockedHandler, methodName, request, Mockito.mock(WebScriptResponse.class));
	}

	static String webScriptIdFor(final HttpMethod httpMethod, final Class<?> clazz, final String method) {
		return String.format("%s.%s.%s", ClassUtils.getQualifiedName(clazz), method, httpMethod.toString()
				.toLowerCase());
	}

	protected AnnotationBasedWebScript webScriptFor(final HttpMethod httpMethod, final Class<?> clazz,
			final String method) {
		final String webScriptId = webScriptIdFor(httpMethod, clazz, method);
		final AnnotationBasedWebScript webScript = (AnnotationBasedWebScript) registry.getWebScript(webScriptId);
		assertNotNull(String.format("Could not find annotation-based WebScript for '%s'", webScriptId), webScript);
		return webScript;
	}
}
