package com.github.dynamicextensionsalfresco.webscripts;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.dynamicextensionsalfresco.webscripts.annotations.HttpMethod;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.webscripts.Runtime;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.extensions.webscripts.WebScriptSession;
import org.springframework.extensions.webscripts.servlet.WebScriptServletRequest;
import org.springframework.extensions.webscripts.servlet.WebScriptServletResponse;

/**
 * Tests the resolving of handler method arguments.
 * 
 * @author Laurens Fridael
 * 
 */
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

	@Test
	public void testHandleWebScriptSession() {
		final Runtime runtime = mock(Runtime.class);
		when(runtime.getSession()).thenReturn(mock(WebScriptSession.class));
		handleGet("/handleWebScriptSession", new MockWebScriptRequest().runtime(runtime));
		verify(handler).handleWebScriptSession(any(WebScriptSession.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testHandleMap() {
		handleGet("/handleMap");
		verify(handler).handleMap(anyMap());
	}

	@Test
	public void testHandleContent() {
		final Content content = mock(Content.class);
		handleGet("/handleContent", new MockWebScriptRequest().content(content));
		verify(handler).handleContent(eq(content));
	}

	@Test
	public void testHandleHttpServletRequest() {
		final WebScriptServletRequest nextRequest = mock(WebScriptServletRequest.class);
		when(nextRequest.getHttpServletRequest()).thenReturn(mock(HttpServletRequest.class));
		handleGet("/handleHttpServletRequest", new MockWebScriptRequest().next(nextRequest));
		verify(handler).handleHttpServletRequest(any(HttpServletRequest.class));
	}

	@Test
	public void testHandleHttpServletResponse() {
		final WebScriptServletResponse nextResponse = mock(WebScriptServletResponse.class);
		when(nextResponse.getHttpServletResponse()).thenReturn(mock(HttpServletResponse.class));
		handleRequest(HttpMethod.GET, "/handleHttpServletResponse", new MockWebScriptRequest(),
				new MockWebScriptResponse().next(nextResponse));
		verify(handler).handleHttpServletResponse(any(HttpServletResponse.class));
	}
}
