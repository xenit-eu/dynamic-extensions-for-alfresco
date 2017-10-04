package com.github.dynamicextensionsalfresco.webscripts;

import com.github.dynamicextensionsalfresco.webscripts.annotations.Attribute;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;

/**
 * Integration test for {@link Attribute} handling.
 * 
 * @author Laurens Fridael
 * 
 */
public class CommandTest extends AbstractWebScriptAnnotationsTest {

	@Autowired
	private CommandHandler handler;

	@Test
	public void testHandleAttribute() {
		handleGet("/handleCommand", new MockWebScriptRequest().param("firstName", "John").param("lastName", "Smith"));
		verify(handler).handleCommand(eq(new Person("John", "Smith")));
	}
}
