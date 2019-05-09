package com.github.dynamicextensionsalfresco.controlpanel;

import com.github.dynamicextensionsalfresco.osgi.Configuration;
import com.github.dynamicextensionsalfresco.webscripts.annotations.*;
import com.github.dynamicextensionsalfresco.webscripts.resolutions.AbstractJsonResolution;
import com.github.dynamicextensionsalfresco.webscripts.resolutions.Resolution;
import org.json.JSONObject;
import org.osgi.framework.Bundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;

/**
 * Handles requests for the Bundle REST API.
 * <p>
 * The the endpoints defined are intended to be called by a REST client rather a web browser. than a web browser.
 * 
 * @author Laurens Fridael
 * 
 */
@Component
@WebScript(baseUri = "/dynamic-extensions/api/bundles", families = "control panel")
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
	public Resolution installBundle(final Content content, @Header("Content-Type") final String contentType) throws Exception {
        if (!configuration.getHotDeployEnabled()) {
            return new JsonMessage(new JSONObject()
                .put("message", "osgi.container.control-panel.bundle-install is disabled"),
                HttpServletResponse.SC_FORBIDDEN);
        } else {
            if (JAR_MIME_TYPE.equalsIgnoreCase(contentType) == false) {
                throw new WebScriptException(HttpServletResponse.SC_BAD_REQUEST,
                    String.format("Can only accept content of type '%s'.", JAR_MIME_TYPE));
            }
            try {
                final Bundle bundle = bundleHelper.installBundleInRepository(content);
                return new JsonMessage(
                    new JSONObject()
                        .put("message", String.format("Installed bundle %s %s", bundle.getSymbolicName(), bundle.getVersion()))
                        .put("bundleId", bundle.getBundleId())
                    , HttpServletResponse.SC_OK);
            } catch (final Exception e) {
                return new JsonMessage(
                    new JSONObject().put("message", e.getMessage()),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
                );
            }
        }
    }

    private static class JsonMessage extends AbstractJsonResolution {
        private final JSONObject jsonObject;

        private JsonMessage(JSONObject jsonObject, Integer status) {
            withStatus(status);
            this.jsonObject = jsonObject;
        }

	    @Override
	    public void resolve() throws Exception {
            getWriter().append(jsonObject.toString(2));
        }
    }
}
