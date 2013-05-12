package nl.runnable.alfresco.webscripts;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Runtime;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.extensions.webscripts.WrappingWebScriptResponse;

public class MockWebScriptResponse implements WrappingWebScriptResponse {

	private WebScriptResponse next;

	private Writer writer;

	private int status;

	public MockWebScriptResponse next(final WebScriptResponse next) {
		this.next = next;
		return this;
	}

	public MockWebScriptResponse writer(final Writer writer) {
		this.writer = writer;
		return this;
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
		// TODO Auto-generated method stub

	}

	@Override
	public void addHeader(final String name, final String value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setContentType(final String contentType) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setContentEncoding(final String contentEncoding) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setCache(final Cache cache) {
		// TODO Auto-generated method stub

	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		// TODO Auto-generated method stub
		return null;
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
