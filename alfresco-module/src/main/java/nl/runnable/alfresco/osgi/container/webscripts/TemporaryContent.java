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

package nl.runnable.alfresco.osgi.container.webscripts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.springframework.extensions.surf.util.Content;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

/**
 * Internal {@link org.springframework.extensions.surf.util.Content} implementation that uses a temporary file for
 * storage. This allows clients to read the content more than once. Call {@link #deleteTempFile()} to remove the
 * temporary file.
 * 
 * @author Laurens Fridael
 * 
 */
class TemporaryContent implements Content {

	private final Content content;

	private File tempFile;

	TemporaryContent(final Content content) {
		Assert.notNull(content, "Content cannot be null.");
		this.content = content;
	}

	private void copyToTempFile(final Content content) throws IOException {
		tempFile = File.createTempFile("bundle", null);
		FileCopyUtils.copy(content.getInputStream(), new FileOutputStream(tempFile));
		content.getInputStream();
	}

	@Override
	public String getMimetype() {
		return content.getMimetype();
	}

	@Override
	public long getSize() {
		return content.getSize();
	}

	@Override
	public String getContent() throws IOException {
		return content.getContent();
	}

	@Override
	public String getEncoding() {
		return content.getEncoding();
	}

	@Override
	public InputStream getInputStream() {
		try {
			if (tempFile == null) {
				copyToTempFile(content);
			}
			return new FileInputStream(tempFile);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Reader getReader() throws IOException {
		final Reader reader;
		final String encoding = getEncoding();
		if (StringUtils.hasText(encoding)) {
			reader = new InputStreamReader(getInputStream(), encoding);
		} else {
			reader = new InputStreamReader(getInputStream());
		}
		return reader;
	}

	/**
	 * Deletes the temporary file.
	 */
	void deleteTempFile() {
		if (tempFile == null) {
			tempFile.delete();
			tempFile = null;
		}
	}

}
