package nl.runnable.alfresco.webscripts;

import static org.mockito.Mockito.*;
import nl.runnable.alfresco.webscripts.annotations.Attribute;

import org.junit.Test;
import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.mockito.Answers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

/**
 * Integration test for {@link Attribute} handling.
 * 
 * @author Laurens Fridael
 * 
 */
@ContextConfiguration(locations = "webscript-integration-test-context.xml", loader = SpringockitoContextLoader.class)
@DirtiesContext
public class AttributeTest extends AbstractWebScriptAnnotationsTest {

	@ReplaceWithMock(defaultAnswer = Answers.CALLS_REAL_METHODS)
	@Autowired
	private AttributeHandler handler;

	@Test
	public void testHandleAttribute() {
		handleGetRequest(handler, "handleAttribute", new MockWebScriptRequest());
		verify(handler).handleAttribute("attribute1", "attribute2");
	}
}
