package nl.runnable.alfresco.webscripts;

import javax.annotation.ManagedBean;

import nl.runnable.alfresco.webscripts.annotations.Header;
import nl.runnable.alfresco.webscripts.annotations.Uri;
import nl.runnable.alfresco.webscripts.annotations.WebScript;

@ManagedBean
@WebScript
public class HeaderHandler {

	@Uri("/handleHeader")
	public void handleHeader(@Header("Content-Type") final String contentType) {
	}
}
