package com.github.dynamicextensionsalfresco.webscripts;

import com.github.dynamicextensionsalfresco.webscripts.annotations.Attribute;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

/**
 * Integration test for {@link Attribute} handling.
 * 
 * @author Laurens Fridael
 * 
 */
public class UriVariableTest extends AbstractWebScriptAnnotationsTest {

	@Autowired
	private UriVariableHandler handler;

	@Test
	public void testHandleUriVariable() {
		handleGet("/handleUriVariable/test");
		assertEquals("test", handler.variable);
	}
}
