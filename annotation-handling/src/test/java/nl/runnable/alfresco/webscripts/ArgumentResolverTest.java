package nl.runnable.alfresco.webscripts;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

public class ArgumentResolverTest extends AbstractWebScriptAnnotationsTest {

	@Autowired
	private ArgumentResolverHandler handler;

	@Test
	public void testHandleWebScriptRequest() {
		handleGet("/handleWebScriptRequest");
		verify(handler).handleWebScriptRequest(any(WebScriptRequest.class));
	}

	@Test
	public void testHandleWebScriptResponse() {
		handleGet("/handleWebScriptResponse");
		verify(handler).handleWebScriptResponse(any(WebScriptResponse.class));
	}
}
