package nl.runnable.alfresco.osgi;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * Web Script for restarting the OSGi framework.
 * <p>
 * Unlike the Control Panel, this Web Script is available from outside the OSGi container.
 * 
 * @author Laurens Fridael
 * 
 */
public class FrameworkRestartWebScript extends AbstractWebScript {

	/* Dependencies */

	private FrameworkService frameworkService;

	/* Main operations */

	@Override
	public void execute(final WebScriptRequest request, final WebScriptResponse response) throws IOException {
		frameworkService.restartFramework();
		sendJsonResponse(response);
	}

	/* Utility operations */

	private void sendJsonResponse(final WebScriptResponse response) throws IOException {
		try {
			final JSONObject result = new JSONObject();
			result.put("status", 200);
			result.put("message", "Restarted OSGi framework.");
			response.setContentType("application/json");
			response.getWriter().write(result.toString(2) + "\n");
		} catch (final JSONException e) {
			throw new RuntimeException(e);
		}
	}

	/* Dependencies */

	public void setFrameworkService(final FrameworkService frameworkService) {
		this.frameworkService = frameworkService;
	}

}
