package nl.runnable.alfresco.webscripts;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import nl.runnable.alfresco.webscripts.annotations.Header;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Integration test for {@link Header} handling.
 * 
 * @author Laurens Fridael
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = SpringockitoContextLoader.class)
public class HeaderTest extends AbstractWebScriptAnnotationsTest {

	/* Dependencies */

	@ReplaceWithMock
	@Autowired
	private HeaderHandler handler;

	/* Main operations */

	@Test
	public void testHandle() {
		final WebScriptRequest request = new MockWebScriptRequestBuilder().header("Content-Type", "application/json")
				.build();
		handleGetRequest(handler, "handle", request);
		verify(handler).handle(eq("application/json"));
	}

}
