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
public class CommandTest extends AbstractWebScriptAnnotationsTest {

	@Autowired
	private CommandHandler handler;

	@Test
	public void testHandleAttribute() {
		handleGet("/handleCommand", new MockWebScriptRequest().param("firstName", "John").param("lastName", "Smith"));
		verify(handler).handleCommand(eq(new Person("John", "Smith")));
	}
}
