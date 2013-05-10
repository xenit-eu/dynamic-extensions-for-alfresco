package nl.runnable.alfresco.webscripts;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import nl.runnable.alfresco.webscripts.annotations.Attribute;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Integration test for {@link Attribute} handling.
 * 
 * @author Laurens Fridael
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
}
