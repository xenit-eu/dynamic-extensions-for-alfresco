package nl.runnable.alfresco.webscripts;

import nl.runnable.alfresco.webscripts.annotations.Header;
import nl.runnable.alfresco.webscripts.annotations.Uri;
import nl.runnable.alfresco.webscripts.annotations.WebScript;

import org.springframework.stereotype.Component;

@Component
@WebScript
public class HeaderHandler {

	@Uri("/handleHeader")
	public void handleHeader(@Header("Content-Type") final String contentType) {
	}
}
