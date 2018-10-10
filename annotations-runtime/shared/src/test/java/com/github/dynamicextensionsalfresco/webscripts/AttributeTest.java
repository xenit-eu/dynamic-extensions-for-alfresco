package com.github.dynamicextensionsalfresco.webscripts;

import com.github.dynamicextensionsalfresco.webscripts.annotations.Attribute;
import org.json.JSONObject;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

/**
 * Integration test for {@link Attribute} handling.
 * 
 * @author Laurens Fridael
 * @author Laurent Van der Linden
 *
 */
public class AttributeTest extends AbstractWebScriptAnnotationsTest {

	@Autowired
	private AttributeHandler handler;

	@Test
	public void testHandleAttributeByName() {
		handleGet("/handleAttributeByName");
		verify(handler).handleAttributeByName("attribute1", "attribute2");
	}

	@Test
	public void testHandleAttributeByType() {
		handleGet("/handleAttributeByType");
		verify(handler).handleAttributeByType(any(Person.class));
	}

	@Test
	public void testHandleAttributeByTypeResolver() {
		handleGet("/handleAttributeByTypeResolver");
		verify(handler).handleAttributeByTypeResolver(any(JSONObject.class));
	}
}
