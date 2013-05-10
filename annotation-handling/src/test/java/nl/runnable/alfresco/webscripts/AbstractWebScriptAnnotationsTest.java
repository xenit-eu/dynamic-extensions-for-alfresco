package nl.runnable.alfresco.webscripts;

import static org.junit.Assert.*;

import java.io.IOException;

import nl.runnable.alfresco.webscripts.annotations.HttpMethod;

import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.extensions.webscripts.Match;
import org.springframework.extensions.webscripts.Registry;
import org.springframework.extensions.webscripts.WebScript;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.ClassUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "webscript-integration-test-context.xml")
public abstract class AbstractWebScriptAnnotationsTest {

	static String webScriptIdFor(final HttpMethod httpMethod, final Class<?> clazz, final String method) {
		return String.format("%s.%s.%s", ClassUtils.getQualifiedName(clazz), method, httpMethod.toString()
				.toLowerCase());
	}

	/* Dependencies */

	@Autowired
	private Registry registry;

	/* Utility operations */

	protected void handleRequest(final HttpMethod httpMethod, final String uri, final MockWebScriptRequest request,
			final WebScriptResponse response) {
		try {
			final Match match = registry.findWebScript(httpMethod.name(), uri);
			assertNotNull(String.format("Could not find annotation-based WebScript for method '%s' and URI '%s'.",
					httpMethod, uri), match);
			final WebScript webScript = match.getWebScript();
			assertTrue("Not an annotation-based Web Script", webScript instanceof AnnotationBasedWebScript);
			webScript.execute(request.setServiceMatch(match), response);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected void handleGet(final String uri, final MockWebScriptRequest request) {
		handleRequest(HttpMethod.GET, uri, request, Mockito.mock(WebScriptResponse.class));
	}

	protected void handleGet(final String uri) {
		handleGet(uri, new MockWebScriptRequest());
	}

	protected AnnotationBasedWebScript webScriptFor(final HttpMethod httpMethod, final Class<?> clazz,
			final String method) {
		final String webScriptId = webScriptIdFor(httpMethod, clazz, method);
		final AnnotationBasedWebScript webScript = (AnnotationBasedWebScript) registry.getWebScript(webScriptId);
		assertNotNull(String.format("Could not find annotation-based WebScript for '%s'", webScriptId), webScript);
		return webScript;
	}

}
