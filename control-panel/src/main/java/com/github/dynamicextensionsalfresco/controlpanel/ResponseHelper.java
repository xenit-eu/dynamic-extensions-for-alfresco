package com.github.dynamicextensionsalfresco.controlpanel;

import com.github.dynamicextensionsalfresco.controlpanel.template.Variables;
import com.github.dynamicextensionsalfresco.osgi.Configuration;
import org.alfresco.repo.content.MimetypeMap;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.extensions.webscripts.WebScriptSession;

import java.io.IOException;
import java.io.PrintWriter;

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

	public void setFlashVariable(final String name, final Object value) {
		request.getRuntime().getSession().setValue(name, value);
	}

	public void flashErrorMessage(final String errorMessage, Exception exception) throws IOException {
		final String acceptedResponseTypes = request.getHeader("Accept");
		if (acceptedResponseTypes != null && acceptedResponseTypes.startsWith(MimetypeMap.MIMETYPE_TEXT_PLAIN)) {
			response.setStatus(500);
			if (exception != null) {
				exception.printStackTrace(new PrintWriter(response.getWriter()));
			} else {
				response.getWriter().write(errorMessage);
			}
		} else {
			setFlashVariable(Variables.ERROR_MESSAGE, errorMessage);
		}
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
}
