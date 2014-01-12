package nl.runnable.alfresco.webscripts;

import nl.runnable.alfresco.spring.Spied;
import nl.runnable.alfresco.webscripts.annotations.Header;
import nl.runnable.alfresco.webscripts.annotations.Uri;

import org.springframework.stereotype.Component;

@Component
@Spied
public class HeaderHandler {

	@Uri("/handleHeader")
	public void handleHeader(@Header("Content-Type") final String contentType) {
	}
}
