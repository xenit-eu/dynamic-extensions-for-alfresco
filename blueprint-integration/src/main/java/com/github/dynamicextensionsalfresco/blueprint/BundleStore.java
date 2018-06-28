package com.github.dynamicextensionsalfresco.blueprint;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.dom4j.Document;
import org.osgi.framework.Bundle;
import org.springframework.extensions.surf.util.Pair;
import org.springframework.extensions.webscripts.ScriptLoader;
import org.springframework.extensions.webscripts.Store;
import org.springframework.extensions.webscripts.WebScript;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import freemarker.cache.TemplateLoader;

/**
 * Provides a {@link Store} implementation backed by a {@link Bundle}.
 * 
 * @author Laurens Fridael
 * 
 */
public class BundleStore implements Store {

	private static final int LAST_MODIFIED_UNKNOWN = -1;

	private final Bundle bundle;

	private boolean secure;

	private final BundleTemplateLoader bundleTemplateLoader;

	private final BundleScriptLoader bundleScriptLoader;

	public BundleStore(final Bundle bundle) {
		Assert.notNull(bundle, "Bundle cannot be null.");
		this.bundle = bundle;
		this.bundleTemplateLoader = new BundleTemplateLoader(bundle);
		this.bundleScriptLoader = new BundleScriptLoader(bundle);
	}

	protected Bundle getBundle() {
		return bundle;
	}

	@Override
	public void init() {
	}

	@Override
	public boolean exists() {
		return true;
	}

	public void setSecure(final boolean secure) {
		this.secure = secure;
		bundleScriptLoader.setSecure(secure);
	}

	@Override
	public boolean isSecure() {
		return secure;
	}

	@Override
	public boolean hasDocument(String documentPath) throws IOException {
		if (documentPath.startsWith("/") == false) {
			documentPath = String.format("/%s", documentPath);
		}
		final URL url = getBundle().getEntry(documentPath);
		return (url != null);
	}

	@Override
	public InputStream getDocument(final String documentPath) throws IOException {
		final URL url = getBundle().getEntry(documentPath);
		return url.openStream();
	}

	@Override
	public long lastModified(final String documentPath) throws IOException {
		long lastModified = LAST_MODIFIED_UNKNOWN;
		final URL url = getBundle().getEntry(documentPath);
		if (url != null) {
			lastModified = url.openConnection().getLastModified();
		}
		return lastModified;
	}

	@Override
	public String getBasePath() {
		return String.format("%s:", bundle.getSymbolicName());
	}

	@Override
	public String[] getAllDocumentPaths() {
		try {
			return getDocumentPaths("/", true, "*");
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String[] getDocumentPaths(String path, final boolean includeSubPaths, final String documentPattern)
			throws IOException {
		if (StringUtils.hasText(path) == false) {
			path = "/";
		}
		if (path.startsWith("/") == false) {
			path = "/" + path;
		}
		final Enumeration<URL> entries = getBundle().findEntries(path, documentPattern, includeSubPaths);
		final List<String> documentPaths = new ArrayList<String>();
		if (entries != null) {
			while (entries.hasMoreElements()) {
				final URL url = entries.nextElement();
				try {
					documentPaths.add(processDocumentPath(url.toURI().getPath()));
				} catch (final URISyntaxException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return documentPaths.toArray(new String[documentPaths.size()]);
	}

	private String processDocumentPath(String path) {
		// Chop off any preceding "/"
		while (path.startsWith("/")) {
			path = path.substring(1);
		}
		return path;
	}

	@Override
	public String[] getDocumentPaths(final String path, final String filePathPattern) throws IOException {
		return getDocumentPaths(path, false, filePathPattern);
	}

	@Override
	public String[] getDescriptionDocumentPaths() throws IOException {
		return getDocumentPaths("/", true, "*.desc.xml");
	}

	@Override
	public String[] getScriptDocumentPaths(final WebScript script) throws IOException {
		final String pattern = String.format("%s.*", script.getDescription().getId());
		return getDocumentPaths("/", false, pattern);
	}

	@Override
	public TemplateLoader getTemplateLoader() {
		return bundleTemplateLoader;
	}

	@Override
	public ScriptLoader getScriptLoader() {
		return bundleScriptLoader;
	}

	// Unsupported

	@Override
	public void createDocument(final String documentPath, final String content) throws IOException {
		throw new UnsupportedOperationException("Not supported");
	}

	@Override
	public void updateDocument(final String documentPath, final String content) throws IOException {
		throw new UnsupportedOperationException("Not supported");
	}

	@Override
	public boolean removeDocument(final String documentPath) throws IOException {
		throw new UnsupportedOperationException("Not supported");
	}

	@Override
	public void createDocuments(final List<Pair<String, Document>> arg0) throws IOException {
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}
}
