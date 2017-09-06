package com.github.dynamicextensionsalfresco.webscripts;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.*;

import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Runtime;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.extensions.webscripts.WrappingWebScriptResponse;

public class MockWebScriptResponse implements WrappingWebScriptResponse {

	private WebScriptResponse next;

	private Writer writer;

	private int status;
	private OutputStream outputStream;

	private final Map<String, List<String>> headers = new HashMap<>();

	public MockWebScriptResponse next(final WebScriptResponse next) {
		this.next = next;
		return this;
	}

	public MockWebScriptResponse writer(final Writer writer) {
		this.writer = writer;
		return this;
	}

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

	/* Simulated operations */

	@Override
	public WebScriptResponse getNext() {
		return next;
	}

	@Override
	public Writer getWriter() throws IOException {
		return writer;
	}

	@Override
	public void setStatus(final int status) {
		this.status = status;
	}

	public int getStatus() {
		return status;
	}

	/* Unimplemented */

	@Override
	public void setHeader(final String name, final String value) {
		this.headers.put(name, new ArrayList<>(Arrays.asList(value)));

	}

	@Override
	public void addHeader(final String name, final String value) {
		if (this.headers.containsKey(name)){
		    this.headers.get(name).add(value);
        }
        else {
		    this.setHeader(name, value);
        }

	}

	@Override
	public void setContentType(final String contentType) {
		this.setHeader("Content-Type", contentType);

	}

	@Override
	public void setContentEncoding(final String contentEncoding) {
		this.setHeader("Content-Encoding", contentEncoding);

	}

	@Override
	public void setCache(final Cache cache) {
		// TODO Auto-generated method stub

	}

	public MockWebScriptResponse setOutputStream(OutputStream stream) {
		this.outputStream = stream;
		return this;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return (this.outputStream != null) ? this.outputStream : new ByteArrayOutputStream();
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

	@Override
	public String encodeScriptUrl(final String url) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String encodeResourceUrl(final String url) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEncodeScriptUrlFunction(final String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEncodeResourceUrlFunction(final String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Runtime getRuntime() {
		// TODO Auto-generated method stub
		return null;
	}

}
