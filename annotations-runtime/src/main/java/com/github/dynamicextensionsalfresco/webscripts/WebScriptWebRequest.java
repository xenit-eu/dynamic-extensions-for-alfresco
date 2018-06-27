package com.github.dynamicextensionsalfresco.webscripts;

import java.security.Principal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import com.github.dynamicextensionsalfresco.webscripts.arguments.CommandArgumentResolver;

import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.web.context.request.WebRequest;

/**
 * {@link WebRequest} adapter for use by {@link CommandArgumentResolver}.
 * 
 * @author Laurens Fridael
 * 
 */
public class WebScriptWebRequest implements WebRequest {

	private final WebScriptRequest request;
	private Map<String, String[]> parameterMap;

	public WebScriptWebRequest(final WebScriptRequest request) {
		this.request = request;
	}

	@Override
	public Object resolveReference(final String key) {
		if (key.equals("request")) {
			return request;
		}
		return null;
	}

	// URL

	@Override
	public String getDescription(final boolean includeClientInfo) {
		return request.getURL();
	}

	@Override
	public String getContextPath() {
		return request.getContextPath();
	}

	// Request headers.

	@Override
	public String getHeader(final String headerName) {
		return request.getHeader(headerName);
	}

	@Override
	public String[] getHeaderValues(final String headerName) {
		return request.getHeaderValues(headerName);
	}

	@Override
	public Iterator<String> getHeaderNames() {
		return Arrays.asList(request.getHeaderNames()).iterator();
	}

	// Request parameters

	@Override
	public String getParameter(final String paramName) {
		return request.getParameter(paramName);
	}

	@Override
	public String[] getParameterValues(final String paramName) {
		return request.getParameterValues(paramName);
	}

	@Override
	public Iterator<String> getParameterNames() {
		return Arrays.asList(request.getParameterNames()).iterator();
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		if (parameterMap == null) {
			parameterMap = new HashMap<String, String[]>();
			for (final String name : request.getParameterNames()) {
				parameterMap.put(name, request.getParameterValues(name));
			}
		}
		return parameterMap;
	}

	// Session

	@Override
	public String getSessionId() {
		request.getRuntime().getSession().getId();
		return null;
	}

	@Override
	public Object getSessionMutex() {
		return null;
	}

	// Security

	@Override
	public boolean isSecure() {
		// Considered secure if not Guest.
		return !request.isGuest();
	}

	// Request attributes, not applicable

	@Override
	public Object getAttribute(final String name, final int scope) {
		return null;
	}

	@Override
	public void setAttribute(final String name, final Object value, final int scope) {
	}

	@Override
	public void removeAttribute(final String name, final int scope) {
	}

	@Override
	public String[] getAttributeNames(final int scope) {
		return null;
	}

	// User settings, not supported

	@Override
	public String getRemoteUser() {
		return null;
	}

	@Override
	public Principal getUserPrincipal() {
		return null;
	}

	@Override
	public boolean isUserInRole(final String role) {
		return false;
	}

	// Not supported

	@Override
	public Locale getLocale() {
		return null;
	}

	@Override
	public boolean checkNotModified(final long lastModifiedTimestamp) {
		return false;
	}

	@Override
	public void registerDestructionCallback(final String name, final Runnable callback, final int scope) {
	}

	@Override
	public boolean checkNotModified(String etag) {
		return false;
	}
}
