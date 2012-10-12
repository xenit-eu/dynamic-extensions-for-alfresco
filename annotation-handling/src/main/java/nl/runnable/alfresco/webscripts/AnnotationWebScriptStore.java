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

package nl.runnable.alfresco.webscripts;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.extensions.webscripts.ScriptLoader;
import org.springframework.extensions.webscripts.Store;
import org.springframework.extensions.webscripts.WebScript;

import freemarker.cache.TemplateLoader;

/**
 * Dummy store implementation used by {@link AnnotationBasedWebScriptBuilder}.
 * 
 * @author Laurens Fridael
 * 
 */
class AnnotationWebScriptStore implements Store {

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

}
