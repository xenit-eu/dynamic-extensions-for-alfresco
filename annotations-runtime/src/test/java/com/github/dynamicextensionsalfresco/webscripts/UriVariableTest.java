package com.github.dynamicextensionsalfresco.webscripts;

import static org.junit.Assert.*;
import com.github.dynamicextensionsalfresco.webscripts.annotations.Attribute;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
