package nl.runnable.alfresco.webscripts;

import static org.mockito.Matchers.*;
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
public class CommandTest extends AbstractWebScriptAnnotationsTest {

	@ReplaceWithMock(defaultAnswer = Answers.CALLS_REAL_METHODS)
	@Autowired
	private CommandHandler handler;

	@Test
	public void testHandleAttribute() {
		handleGetRequest(handler, "handleCommand",
				new MockWebScriptRequest().param("firstName", "John").param("lastName", "Smith"));
		verify(handler).handleCommand(eq(new Person("John", "Smith")));
	}
}
