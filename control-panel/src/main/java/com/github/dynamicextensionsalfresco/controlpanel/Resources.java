package com.github.dynamicextensionsalfresco.controlpanel;

import com.github.dynamicextensionsalfresco.webscripts.annotations.*;
import com.github.dynamicextensionsalfresco.webscripts.support.AbstractBundleResourceHandler;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Web Script for handling requests for static resources for the Web Console.
 * <p>
 * This implementation maps resource paths to static.
 * 
 * @author Laurens Fridael
 * 
 */
@Component
@WebScript(families = "control panel")
@Authentication(AuthenticationType.NONE)
@Transaction(TransactionType.NONE)
public class Resources extends AbstractBundleResourceHandler {

	/* State */

	private final String packagePath;

	/* Main operations */

	public Resources() {
		packagePath = this.getClass().getPackage().getName().replace('.', '/');
	}

	@Uri(value = "/dynamic-extensions/resources/{path}", formatStyle = FormatStyle.ARGUMENT)
	public void handleResources(@UriVariable final String path, final WebScriptRequest request, final WebScriptResponse response) throws Exception {
		handleResource(path, request, response);
	}

	/* Utility operations */

	@Override
	protected String getBundleEntryPath(final String path) {
		return String.format("%s/%s", packagePath, path);
	}
}
