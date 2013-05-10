package nl.runnable.alfresco.webscripts;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import nl.runnable.alfresco.webscripts.annotations.FileField;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.extensions.webscripts.servlet.FormData.FormField;
import org.springframework.extensions.webscripts.servlet.WebScriptServletRequest;

/**
 * Integration test for {@link FileField} handling.
 * 
 * @author Laurens Fridael
 * 
 */
public class FileFieldTest extends AbstractWebScriptAnnotationsTest {

	@Autowired
	private FileFieldHandler handler;

	@Test
	public void testHandleFileField() {
		final WebScriptServletRequest nextRequest = mock(WebScriptServletRequest.class);
		when(nextRequest.getFileField(anyString())).thenReturn(mock(FormField.class));
		handleGet("/handleFileField", new MockWebScriptRequest().next(nextRequest));
		verify(handler).handleFileField(any(FormField.class));
	}

}
