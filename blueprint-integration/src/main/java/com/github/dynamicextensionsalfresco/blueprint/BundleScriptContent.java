package com.github.dynamicextensionsalfresco.blueprint;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import org.springframework.extensions.webscripts.ScriptContent;
import org.springframework.util.Assert;

class BundleScriptContent implements ScriptContent {

	private final URL url;

	private final String path;

	private final boolean secure;

	private final String encoding;

	BundleScriptContent(final URL url, final String path, final boolean secure, final String encoding) {
		Assert.notNull(url, "URL cannot be null.");
		Assert.hasText(encoding, "Encoding cannot be empty.");
		this.url = url;
		this.path = path;
		this.secure = secure;
		this.encoding = encoding;
	}

	protected URL getUrl() {
		return url;
	}

	protected String getEncoding() {
		return encoding;
	}

	@Override
	public InputStream getInputStream() {
		try {
			return getUrl().openStream();
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Reader getReader() {
		try {
			return new InputStreamReader(getUrl().openStream(), getEncoding());
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public String getPathDescription() {
		return path;
	}

	@Override
	public boolean isCachable() {
		return true;
	}

	@Override
	public boolean isSecure() {
		return secure;
	}

}
