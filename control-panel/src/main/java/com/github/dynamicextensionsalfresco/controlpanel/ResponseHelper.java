package nl.runnable.alfresco.controlpanel;

import nl.runnable.alfresco.controlpanel.template.Variables;
import nl.runnable.alfresco.osgi.Configuration;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.extensions.webscripts.WebScriptSession;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Helper for handling responses.
 * 
 * @author Laurens Fridael
 * 
 */
class ResponseHelper extends AbstractResponseHelper {
	ResponseHelper(final WebScriptRequest request, final WebScriptResponse response, Configuration configuration) {
		super(request, response, configuration);
	}

	public ResponseHelper redirectToService(String path) {
		Assert.hasText(path);
		if (path.startsWith("/") == false) {
			path = "/" + path;
		}
		response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY); // 302
		response.setHeader("Location", request.getServiceContextPath() + path);
		return this;
	}

	public void setFlashVariable(final String name, final Object value) {
		request.getRuntime().getSession().setValue(name, value);
	}

	public void flashErrorMessage(final String errorMessage) {
		setFlashVariable(Variables.ERROR_MESSAGE, errorMessage);
	}

	public void flashSuccessMessage(final String successMessage) {
		setFlashVariable(Variables.SUCCESS_MESSAGE, successMessage);
	}

	@SuppressWarnings("unchecked")
	public <T> T getFlashVariable(final String name) {
		final WebScriptSession session = request.getRuntime().getSession();
		final T value = (T) session.getValue(name);
		session.removeValue(name);
		return value;
	}

	public ResponseHelper redirectToBundles() {
		redirectToService("/dynamic-extensions/bundles");
		return this;
	}

	public ResponseHelper redirectToContainer() {
		redirectToService("/dynamic-extensions/container");
		return this;
	}

	public ResponseHelper redirectToBundle(final long bundleId) {
		redirectToService(String.format("/dynamic-extensions/bundles/%d", bundleId));
		return this;
	}

	public ResponseHelper status(final int status, final String message) throws IOException {
		response.setStatus(status);
		if (StringUtils.hasText(message)) {
			response.getWriter().write(message);
		}
		return this;
	}

	public ResponseHelper status(final int status) throws IOException {
		return status(status, null);
	}

	public ResponseHelper noCache() {
		response.setHeader("Cache-Control", "no-cache, nostore");
		return this;
	}
}
