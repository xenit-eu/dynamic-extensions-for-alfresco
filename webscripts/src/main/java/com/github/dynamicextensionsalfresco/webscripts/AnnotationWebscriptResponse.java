package com.github.dynamicextensionsalfresco.webscripts;

import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Runtime;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.extensions.webscripts.WrappingWebScriptResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

/**
 * This class wraps a {@link WebScriptResponse} and captures the status.
 * 
 * @author Laurens Fridael
 * 
 */
public class AnnotationWebscriptResponse implements WrappingWebScriptResponse {

	private final WebScriptResponse response;

	private Integer status;

	/* Main operations */

	public AnnotationWebscriptResponse(final WebScriptResponse response) {
		this.response = response;
	}

	/**
	 * Obtains the status code.
	 * 
	 * @return The status code or null if none has been set.
	 */
	public Integer getStatus() {
		return status;
	}

	@Override
	public WebScriptResponse getNext() {
		if (response instanceof WrappingWebScriptResponse) {
			return ((WrappingWebScriptResponse) response).getNext();
		} else {
			return null;
		}
	}

	/* Delegated operations */

	@Override
	public void setStatus(final int status) {
		this.status = status;
		response.setStatus(status);
	}

	@Override
	public void setHeader(final String name, final String value) {
		response.setHeader(name, value);
	}

	@Override
	public void addHeader(final String name, final String value) {
		response.addHeader(name, value);
	}

	@Override
	public void setContentType(final String contentType) {
		response.setContentType(contentType);
	}

	@Override
	public void setContentEncoding(final String contentEncoding) {
		response.setContentEncoding(contentEncoding);
	}

	@Override
	public void setCache(final Cache cache) {
		response.setCache(cache);
	}

	@Override
	public Writer getWriter() throws IOException {
		return response.getWriter();
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return response.getOutputStream();
	}

	@Override
	public void reset() {
		response.reset();
	}

	@Override
	public String encodeScriptUrl(final String url) {
		return response.encodeScriptUrl(url);
	}

	@Override
	public String encodeResourceUrl(final String url) {
		return response.encodeResourceUrl(url);
	}

	@Override
	public String getEncodeScriptUrlFunction(final String name) {
		return response.getEncodeScriptUrlFunction(name);
	}

	@Override
	public String getEncodeResourceUrlFunction(final String name) {
		return response.getEncodeResourceUrlFunction(name);
	}

	@Override
	public Runtime getRuntime() {
		return response.getRuntime();
	}

}
