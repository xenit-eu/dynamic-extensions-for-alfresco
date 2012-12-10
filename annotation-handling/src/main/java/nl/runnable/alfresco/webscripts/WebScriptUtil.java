package nl.runnable.alfresco.webscripts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.extensions.webscripts.WrappingWebScriptRequest;
import org.springframework.extensions.webscripts.WrappingWebScriptResponse;
import org.springframework.extensions.webscripts.servlet.WebScriptServletRequest;
import org.springframework.extensions.webscripts.servlet.WebScriptServletResponse;

/**
 * Contains Web Script utility methods.
 * 
 * @author Laurens Fridael
 * 
 */
class WebScriptUtil {

	/**
	 * Extracts the {@link HttpServletRequest} from the given {@link WebScriptRequest}.
	 * 
	 * @param request
	 * @return
	 */
	static HttpServletRequest extractHttpServletRequest(final WebScriptRequest request) {
		HttpServletRequest servletRequest = null;
		if (request instanceof WrappingWebScriptRequest) {
			final WrappingWebScriptRequest wrappingRequest = (WrappingWebScriptRequest) request;
			final WebScriptRequest nextRequest = wrappingRequest.getNext();
			if (nextRequest instanceof WebScriptServletRequest) {
				servletRequest = ((WebScriptServletRequest) nextRequest).getHttpServletRequest();
			}
		}
		return servletRequest;
	}

	/**
	 * Extracts the {@link HttpServletResponse} from the given {@link WebScriptResponse}.
	 * 
	 * @param request
	 * @return
	 */
	static HttpServletResponse extractHttpServletResponse(final WebScriptResponse request) {
		HttpServletResponse servletResponse = null;
		if (request instanceof WrappingWebScriptResponse) {
			final WrappingWebScriptResponse wrappingResponse = (WrappingWebScriptResponse) request;
			final WebScriptResponse nextResponse = wrappingResponse.getNext();
			if (nextResponse instanceof WebScriptServletResponse) {
				servletResponse = ((WebScriptServletResponse) nextResponse).getHttpServletResponse();
			}
		}
		return servletResponse;
	}

	private WebScriptUtil() {
	}
}
