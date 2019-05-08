package com.github.dynamicextensionsalfresco.blueprint;

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
