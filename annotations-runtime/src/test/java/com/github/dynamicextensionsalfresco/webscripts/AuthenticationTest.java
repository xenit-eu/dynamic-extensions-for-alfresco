package com.github.dynamicextensionsalfresco.webscripts;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.extensions.webscripts.Description;
import org.springframework.extensions.webscripts.Match;
import org.springframework.extensions.webscripts.UriIndex;

import static org.junit.Assert.assertEquals;

/**
 * Verify @Authentication can be overridden on a method basis.
 *
 * @author Laurent Van der Linden
 */
public class AuthenticationTest extends AbstractWebScriptAnnotationsTest {

	/* Dependencies */

	@Autowired
	private UriIndex uriIndex;

	/* Main operations */

	@Test
	public void testAuthenticationConfiguration() {
        final Match open = uriIndex.findWebScript("GET", "/open");
        assertEquals(Description.RequiredAuthentication.none, open.getWebScript().getDescription().getRequiredAuthentication());

        final Match standard = uriIndex.findWebScript("GET", "/standard");
        assertEquals(Description.RequiredAuthentication.admin, standard.getWebScript().getDescription().getRequiredAuthentication());
    }

}
