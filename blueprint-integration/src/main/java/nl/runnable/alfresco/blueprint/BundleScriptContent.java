/*
Copyright (c) 2012, Runnable
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
 * Neither the name of Runnable nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package nl.runnable.alfresco.blueprint;

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
