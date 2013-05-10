package nl.runnable.alfresco.webscripts;

import static org.mockito.Mockito.*;
import nl.runnable.alfresco.webscripts.annotations.Before;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Integration test for {@link Before} handling.
 * 
 * @author Laurens Fridael
 * 
 */
public class BeforeTest extends AbstractWebScriptAnnotationsTest {

	@Autowired
	private BeforeHandler handler;

	@Test
	public void testHandleBefore() {
		handleGet("/handleBefore");
		verify(handler).handleBefore("attribute");
	}
}
