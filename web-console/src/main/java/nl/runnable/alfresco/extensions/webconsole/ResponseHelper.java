package nl.runnable.alfresco.extensions.webconsole;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Helper for handling responses.
 * 
 * @author Laurens Fridael
 * 
 */
class ResponseHelper {
	private final WebScriptRequest request;

	private final WebScriptResponse response;

	ResponseHelper(final WebScriptRequest request, final WebScriptResponse response) {
		this.request = request;
		this.response = response;
	}

	public void redirectToService(String path) {
		Assert.hasText(path);
		if (path.startsWith("/") == false) {
			path = "/" + path;
		}
		response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY); // 302
		response.setHeader("Location", request.getServiceContextPath() + path);
	}

	public void redirectToIndex() {
		redirectToService("/dynamic-extensions");
	}

	public void status(final int status, final String message) throws IOException {
		response.setStatus(status);
		if (StringUtils.hasText(message)) {
			response.getWriter().write(message);
		}
	}

	public void status(final int status) throws IOException {
		status(status, null);
	}
}
