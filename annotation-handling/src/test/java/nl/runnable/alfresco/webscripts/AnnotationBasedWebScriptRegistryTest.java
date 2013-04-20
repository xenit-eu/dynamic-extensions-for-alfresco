package nl.runnable.alfresco.webscripts;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.extensions.webscripts.Description;
import org.springframework.extensions.webscripts.Description.RequiredAuthentication;
import org.springframework.extensions.webscripts.Description.RequiredTransaction;
import org.springframework.extensions.webscripts.Description.TransactionCapability;
import org.springframework.extensions.webscripts.WebScript;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class AnnotationBasedWebScriptRegistryTest {

	private AnnotationBasedWebScriptRegistry registry;

	@Autowired
	public void setRegistry(final AnnotationBasedWebScriptRegistry registry) {
		this.registry = registry;
	}

	@Test
	public void testGetWebScripts() {
		final List<WebScript> webScripts = new ArrayList<WebScript>(registry.getWebScripts());
		assertEquals(2, webScripts.size());
		final List<String> methods = Arrays.asList("GET", "POST");
		final List<String> handlerMethods = Arrays.asList("handleGetRequest", "handlePostRequest");
		final List<String[]> uris = Arrays.asList(new String[] { "/path/to/resource/{id}" },
				new String[] { "/path/to/post" });
		for (int i = 0; i < webScripts.size(); i++) {
			final WebScript webScript = webScripts.get(i);
			final Description description = webScript.getDescription();
			assertEquals(methods.get(i), description.getMethod());
			assertArrayEquals(uris.get(i), description.getURIs());
			assertEquals("nl.runnable.alfresco.webscripts.ExampleWebScript." + handlerMethods.get(i) + "."
					+ methods.get(i).toLowerCase(), description.getId());

			assertEquals("ExampleWebScript", description.getShortName());
			assertEquals("Example web script used for test cases.", description.getDescription());
			assertEquals("admin", description.getRunAs());
			assertEquals(RequiredAuthentication.user, description.getRequiredAuthentication());
			assertEquals(RequiredTransaction.requiresnew, description.getRequiredTransaction());
			assertEquals(TransactionCapability.readonly, description.getRequiredTransactionParameters().getCapability());
		}
	}

}
