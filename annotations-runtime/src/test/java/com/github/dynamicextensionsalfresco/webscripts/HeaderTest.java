package com.github.dynamicextensionsalfresco.webscripts;

import com.github.dynamicextensionsalfresco.webscripts.annotations.Header;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;

/**
 * Integration test for {@link Header} handling.
 * 
 * @author Laurens Fridael
 * 
 */
public class HeaderTest extends AbstractWebScriptAnnotationsTest {

	/* Dependencies */

	@Autowired
	private HeaderHandler handler;

	/* Main operations */

	@Test
	public void testHandle() {
		handleGet("/handleHeader", new MockWebScriptRequest().header("Content-Type", "application/json"));
		verify(handler).handleHeader(eq("application/json"));
	}

}
