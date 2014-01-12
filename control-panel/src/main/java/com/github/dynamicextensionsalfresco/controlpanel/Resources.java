package com.github.dynamicextensionsalfresco.controlpanel;

import java.io.IOException;

import com.github.dynamicextensionsalfresco.webscripts.annotations.FormatStyle;
import com.github.dynamicextensionsalfresco.webscripts.annotations.Uri;
import com.github.dynamicextensionsalfresco.webscripts.annotations.UriVariable;
import com.github.dynamicextensionsalfresco.webscripts.annotations.WebScript;

import com.github.dynamicextensionsalfresco.webscripts.support.AbstractBundleResourceHandler;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.stereotype.Component;

/**
 * Web Script for handling requests for static resources for the Web Console.
 * <p>
 * This implementation maps resource paths to static.
 * 
 * @author Laurens Fridael
 * 
 */
@Component
@WebScript
public class Resources extends AbstractBundleResourceHandler {

	/* State */

	private final String packagePath;

	/* Main operations */

	public Resources() {
		packagePath = this.getClass().getPackage().getName().replace('.', '/');
	}

	@Uri(value = "/dynamic-extensions/resources/{path}", formatStyle = FormatStyle.ARGUMENT)
	public void handleResources(@UriVariable final String path, final WebScriptResponse response) throws IOException {
		handleResource(path, response);
	}

	/* Utility operations */

	@Override
	protected String getBundleEntryPath(final String path) {
		return String.format("%s/%s", packagePath, path);
	}
}
