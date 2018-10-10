package com.github.dynamicextensionsalfresco.controlpanel;

import com.github.dynamicextensionsalfresco.osgi.Configuration;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import javax.servlet.http.HttpServletResponse;

/**
 * Common response helper for API and MVC handlers.
 *
 * @author Laurent Van der Linden
 */
public abstract class AbstractResponseHelper {
	protected final WebScriptRequest request;

	protected final WebScriptResponse response;

	protected final Configuration configuration;

	protected AbstractResponseHelper(WebScriptRequest request, WebScriptResponse response, Configuration configuration) {
		this.request = request;
		this.response = response;
		this.configuration = configuration;
	}

	public void checkBundleInstallConfiguration() {
		if (!configuration.getHotDeployEnabled()) {
			throw new WebScriptException(
					HttpServletResponse.SC_FORBIDDEN,
					"osgi.container.control-panel.bundle-install is disabled"
			);
		}
	}
}
