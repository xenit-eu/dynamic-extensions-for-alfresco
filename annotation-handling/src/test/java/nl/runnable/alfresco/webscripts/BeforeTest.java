package nl.runnable.alfresco.webscripts;

import static org.mockito.Mockito.*;
import nl.runnable.alfresco.webscripts.annotations.Before;

import org.junit.Test;
import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.mockito.Answers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

/**
 * Integration test for {@link Before} handling.
 * 
 * @author Laurens Fridael
 * 
 */
@ContextConfiguration(locations = "webscript-integration-test-context.xml", loader = SpringockitoContextLoader.class)
@DirtiesContext
public class BeforeTest extends AbstractWebScriptAnnotationsTest {

	@ReplaceWithMock(defaultAnswer = Answers.CALLS_REAL_METHODS)
	@Autowired
	private BeforeHandler handler;

	@Test
	public void testHandleAttribute() {
		handleGetRequest(handler, "handleBefore", new MockWebScriptRequest());
		verify(handler).handleBefore("attribute");
	}
}
