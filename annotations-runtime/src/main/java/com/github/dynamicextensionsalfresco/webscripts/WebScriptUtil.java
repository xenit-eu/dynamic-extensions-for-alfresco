package com.github.dynamicextensionsalfresco.webscripts;

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
public class WebScriptUtil {

	public static WebScriptServletRequest extractWebScriptServletRequest(final WebScriptRequest request) {
		WebScriptServletRequest webScriptServletRequest = null;
		if (request instanceof WrappingWebScriptRequest) {
			final WebScriptRequest nextRequest = ((WrappingWebScriptRequest) request).getNext();
			if (nextRequest instanceof WebScriptServletRequest) {
				webScriptServletRequest = ((WebScriptServletRequest) nextRequest);
			} else if (nextRequest != null) {
				extractWebScriptServletRequest(nextRequest);
			}
		}
		return webScriptServletRequest;
	}

	/**
	 * Extracts the {@link HttpServletRequest} from the given {@link WebScriptRequest}.
	 * 
	 * @param request
	 * @return
	 */
	public static HttpServletRequest extractHttpServletRequest(final WebScriptRequest request) {
		HttpServletRequest servletRequest = null;
		if (request instanceof WrappingWebScriptRequest) {
			final WebScriptRequest nextRequest = ((WrappingWebScriptRequest) request).getNext();
			if (nextRequest instanceof WebScriptServletRequest) {
				servletRequest = ((WebScriptServletRequest) nextRequest).getHttpServletRequest();
			} else if (nextRequest != null) {
				servletRequest = extractHttpServletRequest(nextRequest);
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
	public static HttpServletResponse extractHttpServletResponse(final WebScriptResponse request) {
		HttpServletResponse servletResponse = null;
		if (request instanceof WrappingWebScriptResponse) {
			final WebScriptResponse nextResponse = ((WrappingWebScriptResponse) request).getNext();
			if (nextResponse instanceof WebScriptServletResponse) {
				servletResponse = ((WebScriptServletResponse) nextResponse).getHttpServletResponse();
			} else if (nextResponse != null) {
				servletResponse = extractHttpServletResponse(nextResponse);
			}
		}
		return servletResponse;
	}

	private WebScriptUtil() {
	}
}
