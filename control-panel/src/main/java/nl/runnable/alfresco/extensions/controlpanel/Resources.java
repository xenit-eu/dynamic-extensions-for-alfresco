package nl.runnable.alfresco.extensions.controlpanel;

import java.io.IOException;

import javax.annotation.ManagedBean;

import nl.runnable.alfresco.webscripts.annotations.FormatStyle;
import nl.runnable.alfresco.webscripts.annotations.Uri;
import nl.runnable.alfresco.webscripts.annotations.UriVariable;
import nl.runnable.alfresco.webscripts.annotations.WebScript;

import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * Web Script for handling requests for static resources for the Web Console.
 * <p>
 * This implementation maps resource paths to static.
 * 
 * @author Laurens Fridael
 * 
 */
@ManagedBean
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
