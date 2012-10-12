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

import java.net.URL;

import org.osgi.framework.Bundle;
import org.springframework.extensions.webscripts.ScriptContent;
import org.springframework.extensions.webscripts.ScriptLoader;
import org.springframework.util.Assert;

class BundleScriptLoader implements ScriptLoader {

	private static final String DEFAULT_SCRIPT_ENCODING = "ISO-8859-1";

	private final Bundle bundle;

	private boolean secure;

	private String encoding;

	BundleScriptLoader(final Bundle bundle) {
		Assert.notNull(bundle, "Bundle cannot be null.");
		this.bundle = bundle;
	}

	protected Bundle getBundle() {
		return bundle;
	}

	public void setSecure(final boolean secure) {
		this.secure = secure;
	}

	protected boolean isSecure() {
		return secure;
	}

	public void setEncoding(final String encoding) {
		this.encoding = encoding;
	}

	protected String getEncoding() {
		return encoding;
	}

	@Override
	public ScriptContent getScript(final String path) {
		ScriptContent scriptContent = null;
		try {
			URL url = null;
			if (getBundle().getState() == Bundle.ACTIVE) {
				url = getBundle().getEntry(path);
			}
			if (url != null) {
				scriptContent = new BundleScriptContent(url, path, secure, DEFAULT_SCRIPT_ENCODING);
			}
		} catch (final IllegalStateException e) {
			/* Bundle is uninstalled or entry is otherwise not available. We ignore this. */
		}
		return scriptContent;
	}

}
