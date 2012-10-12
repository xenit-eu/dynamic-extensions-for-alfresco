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
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import org.osgi.framework.Bundle;
import org.springframework.util.Assert;

import freemarker.cache.TemplateLoader;

/**
 * Provides a Freemarker {@link TemplateLoader} that resolves resources in a {@link Bundle}.
 * 
 * @author Laurens Fridael
 * 
 */
class BundleTemplateLoader implements TemplateLoader {

	private static final int LAST_MODIFIED_UNKNOWN = -1;

	private final Bundle bundle;

	BundleTemplateLoader(final Bundle bundle) {
		Assert.notNull(bundle, "Bundle cannot be null.");
		this.bundle = bundle;
	}

	protected Bundle getBundle() {
		return bundle;
	}

	@Override
	public Object findTemplateSource(final String name) throws IOException {
		Object templateSource = null;
		if (getBundle().getState() == Bundle.ACTIVE) {
			templateSource = getBundle().getEntry(name);
		}
		return templateSource;
	}

	@Override
	public long getLastModified(final Object templateSource) {
		long lastModified = LAST_MODIFIED_UNKNOWN;
		if (templateSource instanceof URL) {
			try {
				final URL url = (URL) templateSource;
				lastModified = url.openConnection().getLastModified();
			} catch (final IOException e) {
				throw new RuntimeException(e);
			}
		}
		return lastModified;
	}

	@Override
	public Reader getReader(final Object templateSource, final String encoding) throws IOException {
		Reader reader = null;
		if (templateSource instanceof URL) {
			final URL url = (URL) templateSource;
			reader = new InputStreamReader(url.openStream(), encoding);
		}
		return reader;
	}

	@Override
	public void closeTemplateSource(final Object templateSource) throws IOException {
		// This implementation does not have to do anything.
	}

}
