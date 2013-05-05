package nl.runnable.alfresco.webscripts;

import static org.junit.Assert.*;
import nl.runnable.alfresco.webscripts.annotations.Attribute;
import nl.runnable.alfresco.webscripts.annotations.HttpMethod;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

/**
 * Integration test for {@link Attribute} handling.
 * 
 * @author Laurens Fridael
 * 
 */
@ContextConfiguration(locations = "webscript-integration-test-context.xml")
@DirtiesContext
public class UriVariableTest extends AbstractWebScriptAnnotationsTest {

	@Autowired
	private UriVariableHandler handler;

	@Test
	public void testHandleUriVariable() {
		handleGetRequest(HttpMethod.GET, "/handleUriVariable/test");
		assertEquals("test", handler.variable);
	}
}
