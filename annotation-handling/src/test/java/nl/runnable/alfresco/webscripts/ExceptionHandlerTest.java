package nl.runnable.alfresco.webscripts;

import static org.junit.Assert.*;

import javax.inject.Inject;

import nl.runnable.alfresco.webscripts.annotations.HttpMethod;

import org.junit.Test;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(locations = "webscript-integration-test-context.xml", loader = SpringockitoContextLoader.class)
@DirtiesContext
public class ExceptionHandlerTest extends AbstractWebScriptAnnotationsTest {

	@Inject
	private ExceptionHandlerExample handler;

	@Test
	public void testHandleException() {
		handleGetRequest(HttpMethod.GET, "/throwIllegalArgumentException");
		assertNotNull(handler.illegalArgumentException);
	}
}
