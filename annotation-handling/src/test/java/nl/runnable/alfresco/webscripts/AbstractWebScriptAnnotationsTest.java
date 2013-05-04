package nl.runnable.alfresco.webscripts;

import static org.junit.Assert.*;

import java.io.IOException;

import nl.runnable.alfresco.webscripts.annotations.HttpMethod;

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

	protected void handleRequest(final HttpMethod httpMethod, final Object handler, final String methodName,
			final WebScriptRequest request, final WebScriptResponse response) {
		try {
			final AnnotationBasedWebScript webScript = webScriptFor(httpMethod, RequestParamHandler.class, methodName);
			webScript.execute(request, response);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
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
