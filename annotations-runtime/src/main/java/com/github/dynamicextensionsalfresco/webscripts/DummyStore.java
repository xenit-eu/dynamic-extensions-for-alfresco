package com.github.dynamicextensionsalfresco.webscripts;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.dom4j.Document;
import org.springframework.extensions.surf.util.Pair;
import org.springframework.extensions.webscripts.ScriptLoader;
import org.springframework.extensions.webscripts.Store;
import org.springframework.extensions.webscripts.WebScript;

import freemarker.cache.TemplateLoader;

/**
 * Dummy store implementation used by {@link AnnotationWebScriptBuilder}.
 * 
 * @author Laurens Fridael
 * 
 */
public class DummyStore implements Store {

	private boolean secure = false;

	@Override
	public void init() {
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public String getBasePath() {
		return "/";
	}

	public void setSecure(final boolean secure) {
		this.secure = secure;
	}

	@Override
	public boolean isSecure() {
		return secure;
	}

	@Override
	public String[] getDocumentPaths(final String path, final boolean includeSubPaths, final String documentPattern)
			throws IOException {
		return null;
	}

	@Override
	public String[] getDocumentPaths(final String path, final String filePathPattern) throws IOException {
		return null;
	}

	@Override
	public String[] getDescriptionDocumentPaths() throws IOException {
		return null;
	}

	@Override
	public String[] getScriptDocumentPaths(final WebScript script) throws IOException {
		return null;
	}

	@Override
	public String[] getAllDocumentPaths() {
		return null;
	}

	@Override
	public long lastModified(final String documentPath) throws IOException {
		return 0;
	}

	@Override
	public boolean hasDocument(final String documentPath) throws IOException {
		return false;
	}

	@Override
	public InputStream getDocument(final String documentPath) throws IOException {
		return null;
	}

	@Override
	public void createDocument(final String documentPath, final String content) throws IOException {
	}

	@Override
	public void updateDocument(final String documentPath, final String content) throws IOException {
	}

	@Override
	public boolean removeDocument(final String documentPath) throws IOException {
		return false;
	}

	@Override
	public TemplateLoader getTemplateLoader() {
		return null;
	}

	@Override
	public ScriptLoader getScriptLoader() {
		return null;
	}

	// New in 1.0.0

	@Override
	public boolean isReadOnly() {
		return true;
	}

	// New in 1.1.0 or 1.2.0

	@Override
	public void createDocuments(final List<Pair<String, Document>> arg0) throws IOException {
	}

}
