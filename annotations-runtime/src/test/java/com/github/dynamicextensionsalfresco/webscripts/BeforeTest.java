package com.github.dynamicextensionsalfresco.webscripts;

import com.github.dynamicextensionsalfresco.webscripts.annotations.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.Mockito.verify;

/**
 * Integration test for {@link Before} handling.
 * 
 * @author Laurens Fridael
 * 
 */
public class BeforeTest extends AbstractWebScriptAnnotationsTest {

	@Autowired
	private BeforeHandler handler;

	@Test
	public void testHandleBefore() {
		handleGet("/handleBefore");
		verify(handler).handleBefore("attribute");
	}
}
