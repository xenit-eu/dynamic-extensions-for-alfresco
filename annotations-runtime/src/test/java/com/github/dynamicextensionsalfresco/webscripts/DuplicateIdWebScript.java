package nl.runnable.alfresco.webscripts;

import nl.runnable.alfresco.webscripts.annotations.Uri;
import nl.runnable.alfresco.webscripts.annotations.WebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * Webscript with invalid duplicate @Uri method names.
 *
 * @author Laurent Van der Linden
 */
@WebScript
public class DuplicateIdWebScript {
	@Uri(value = "/twins")
	public void twins() {}

	@Uri(value = "/twins")
	public void twins(final WebScriptRequest request) {}
}
