package nl.runnable.alfresco.controlpanel;

import nl.runnable.alfresco.osgi.Configuration;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.Bundle;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

class JsonResponseHelper extends AbstractResponseHelper {

	private static final String JSON_MIME_TYPE = "application/json";

	/* Main operations */

	protected JsonResponseHelper(WebScriptRequest request, WebScriptResponse response, Configuration configuration) {
		super(request, response, configuration);
	}

	public void sendMessage(final int status, final String message) throws IOException {
		try {
			response.setStatus(status);
			response.setContentType(JSON_MIME_TYPE);
			final JSONObject json = new JSONObject();
			json.put("message", message);
			writeJson(json);
		} catch (final JSONException e) {
			throw new RuntimeException(e);
		}
	}

	public void sendBundleInstalledMessage(final Bundle bundle) throws IOException {
		try {
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType(JSON_MIME_TYPE);
			final JSONObject json = new JSONObject();
			json.put("message", String.format("Installed bundle %s %s", bundle.getSymbolicName(), bundle.getVersion()));
			json.put("bundleId", bundle.getBundleId());
			writeJson(json);
		} catch (final JSONException e) {
			throw new RuntimeException(e);
		}
	}

	protected void writeJson(final JSONObject json) throws IOException, JSONException {
		response.getWriter().write(json.toString(2));
	}

}
