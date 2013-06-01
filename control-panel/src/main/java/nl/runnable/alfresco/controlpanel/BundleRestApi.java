package nl.runnable.alfresco.controlpanel;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import nl.runnable.alfresco.webscripts.annotations.Attribute;
import nl.runnable.alfresco.webscripts.annotations.Authentication;
import nl.runnable.alfresco.webscripts.annotations.AuthenticationType;
import nl.runnable.alfresco.webscripts.annotations.Header;
import nl.runnable.alfresco.webscripts.annotations.HttpMethod;
import nl.runnable.alfresco.webscripts.annotations.Uri;
import nl.runnable.alfresco.webscripts.annotations.WebScript;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.stereotype.Component;

/**
 * Handles requests for the Bundle REST API.
 * <p>
 * The the endpoints defined are intended to be called by a REST client rather a web browser. than a web browser.
 * 
 * @author Laurens Fridael
 * 
 */
@Component
@WebScript(baseUri = "/dynamic-extensions/api/bundles")
@Authentication(value = AuthenticationType.ADMIN)
public class BundleRestApi {

	private static final String JAR_MIME_TYPE = "application/java-archive";

	/* Dependencies */

	@Autowired
	private BundleHelper bundleHelper;

	/* Main operations */

	@Uri(method = HttpMethod.POST)
	public void installBundle(final Content content, @Header("Content-Type") final String contentType,
			@Attribute final JsonResponseHelper response) throws IOException {
		if (JAR_MIME_TYPE.equalsIgnoreCase(contentType) == false) {
			response.sendMessage(HttpServletResponse.SC_BAD_REQUEST,
					String.format("Can only accept content of type '%s'.", JAR_MIME_TYPE));
		}
		try {
			final Bundle bundle = bundleHelper.installBundleInRepository(content);
			response.sendBundleInstalledMessage(bundle);
		} catch (final BundleException e) {
			response.sendMessage(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		}

	}

	/* Utility operations */

	@Attribute
	protected JsonResponseHelper getJsonResponseHelper(final WebScriptResponse response) {
		return new JsonResponseHelper(response);
	}

}
