package com.github.dynamicextensionsalfresco.controlpanel;

import com.github.dynamicextensionsalfresco.osgi.Configuration;
import com.github.dynamicextensionsalfresco.webscripts.annotations.*;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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

	@Autowired
	private Configuration configuration;

	/* Main operations */

	@Uri(method = HttpMethod.POST)
	public void installBundle(final Content content, @Header("Content-Type") final String contentType,
			@Attribute final JsonResponseHelper response) throws IOException, BundleException {
		response.checkBundleInstallConfiguration();
		if (JAR_MIME_TYPE.equalsIgnoreCase(contentType) == false) {
			throw new WebScriptException(HttpServletResponse.SC_BAD_REQUEST,
					String.format("Can only accept content of type '%s'.", JAR_MIME_TYPE));
		}
        final Bundle bundle;
        try {
            bundle = bundleHelper.installBundleInRepository(content);
            response.sendBundleInstalledMessage(bundle);
        } catch (BundleException e) {
            response.sendMessage(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
	}

	/* Utility operations */

	@Attribute
	protected JsonResponseHelper getJsonResponseHelper(final WebScriptRequest request, final WebScriptResponse response) {
		return new JsonResponseHelper(request, response, configuration);
	}

}
