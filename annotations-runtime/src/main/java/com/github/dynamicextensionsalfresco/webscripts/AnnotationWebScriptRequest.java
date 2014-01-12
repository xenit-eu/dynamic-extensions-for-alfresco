package com.github.dynamicextensionsalfresco.webscripts;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.webscripts.Description.FormatStyle;
import org.springframework.extensions.webscripts.Match;
import org.springframework.extensions.webscripts.Runtime;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WrappingWebScriptRequest;
import org.springframework.util.Assert;

public class AnnotationWebScriptRequest implements WrappingWebScriptRequest {

	/* State */

	private final WebScriptRequest webScriptRequest;

	private final Map<String, Object> model = new LinkedHashMap<String, Object>();

	private Throwable thrownException;

	AnnotationWebScriptRequest(final WebScriptRequest webScriptRequest) {
		Assert.notNull(webScriptRequest);
		this.webScriptRequest = webScriptRequest;
	}

	/* State */

	public WebScriptRequest getWebScriptRequest() {
		return webScriptRequest;
	}

	public Map<String, Object> getModel() {
		return model;
	}

	void setThrownException(final Throwable thrownException) {
		this.thrownException = thrownException;
	}

	public Throwable getThrownException() {
		return thrownException;
	}

	@Override
	public WebScriptRequest getNext() {
		if (webScriptRequest instanceof WrappingWebScriptRequest) {
			return ((WrappingWebScriptRequest) webScriptRequest).getNext();
		} else {
			return null;
		}
	}

	/* Delegate methods */

	@Override
	public Match getServiceMatch() {
		return webScriptRequest.getServiceMatch();
	}

	@Override
	public String getServerPath() {
		return webScriptRequest.getServerPath();
	}

	@Override
	public String getContextPath() {
		return webScriptRequest.getContextPath();
	}

	@Override
	public String getServiceContextPath() {
		return webScriptRequest.getServiceContextPath();
	}

	@Override
	public String getServicePath() {
		return webScriptRequest.getServicePath();
	}

	@Override
	public String getURL() {
		return webScriptRequest.getURL();
	}

	@Override
	public String getPathInfo() {
		return webScriptRequest.getPathInfo();
	}

	@Override
	public String getQueryString() {
		return webScriptRequest.getQueryString();
	}

	@Override
	public String[] getParameterNames() {
		return webScriptRequest.getParameterNames();
	}

	@Override
	public String getParameter(final String name) {
		return webScriptRequest.getParameter(name);
	}

	@Override
	public String[] getParameterValues(final String name) {
		return webScriptRequest.getParameterValues(name);
	}

	@Override
	public String[] getHeaderNames() {
		return webScriptRequest.getHeaderNames();
	}

	@Override
	public String getHeader(final String name) {
		return webScriptRequest.getHeader(name);
	}

	@Override
	public String[] getHeaderValues(final String name) {
		return webScriptRequest.getHeaderValues(name);
	}

	@Override
	public String getExtensionPath() {
		return webScriptRequest.getExtensionPath();
	}

	@Override
	public String getContentType() {
		return webScriptRequest.getContentType();
	}

	@Override
	public Content getContent() {
		return webScriptRequest.getContent();
	}

	@Override
	public Object parseContent() {
		return webScriptRequest.parseContent();
	}

	@Override
	public boolean isGuest() {
		return webScriptRequest.isGuest();
	}

	@Override
	public String getFormat() {
		return webScriptRequest.getFormat();
	}

	@Override
	public FormatStyle getFormatStyle() {
		return webScriptRequest.getFormatStyle();
	}

	@Override
	public String getAgent() {
		return webScriptRequest.getAgent();
	}

	@Override
	public String getJSONCallback() {
		return webScriptRequest.getJSONCallback();
	}

	@Override
	public boolean forceSuccessStatus() {
		return webScriptRequest.forceSuccessStatus();
	}

	@Override
	public Runtime getRuntime() {
		return webScriptRequest.getRuntime();
	}
}
