package com.github.dynamicextensionsalfresco.webscripts;

import static org.junit.Assert.*;

import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.springframework.extensions.webscripts.JaxRSUriIndex;
import org.springframework.extensions.webscripts.Match;
import org.springframework.extensions.webscripts.WebScript;

public class WebScriptUriRegistryTest {

	private WebScriptUriRegistry webScriptUriRegistry;

	private WebScript webScript;

	@Before
	public void setup() {
		webScriptUriRegistry = new WebScriptUriRegistry();
		webScriptUriRegistry.setUriIndex(new JaxRSUriIndex());
		webScript = new ExampleWebScript(HttpServletResponse.SC_OK, "/test");
		webScriptUriRegistry.registerWebScript(webScript);
	}

	@Test
	public void testWebScriptRegistration() {
		final Match match = webScriptUriRegistry.findWebScript("GET", "/test");
		assertNotNull(match);
		assertNotSame(webScript, match.getWebScript());
		assertSame(webScript.getDescription(), match.getWebScript().getDescription());
		assertSame(webScript, ((WebScriptProxy) match.getWebScript()).getWrappedWebScript());
	}

	@Test
	public void testWebScriptRegistrationIsPersistentWhenCleared() {
		webScriptUriRegistry.clear();
		final Match match = webScriptUriRegistry.findWebScript("GET", "/test");
		assertNotNull(match);
	}

	@Test
	public void testWebScriptUnregistration() {
		webScriptUriRegistry.unregisterWebScript(webScript);
		final Match match = webScriptUriRegistry.findWebScript("GET", "/test");
		assertNotNull(match);
		final WebScriptProxy webScriptProxy = (WebScriptProxy) match.getWebScript();
		assertNotNull(webScriptProxy.getWrappedWebScript());
		assertNotSame(webScript, webScriptProxy.getWrappedWebScript());
		assertSame(webScript.getDescription(), webScriptProxy.getDescription());
	}

	@Test
	public void testWebScriptReregistration() {
		webScriptUriRegistry.unregisterWebScript(webScript);
		webScriptUriRegistry.registerWebScript(webScript);
		testWebScriptRegistration();
	}

	@Test
	public void testWebScriptUnregistrationIsPermanentWhenCleared() {
		webScriptUriRegistry.unregisterWebScript(webScript);
		webScriptUriRegistry.clear();
		final Match match = webScriptUriRegistry.findWebScript("GET", "/test");
		assertNull(match);
	}

}
