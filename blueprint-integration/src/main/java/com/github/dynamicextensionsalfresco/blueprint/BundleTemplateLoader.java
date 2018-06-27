package com.github.dynamicextensionsalfresco.blueprint;

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
