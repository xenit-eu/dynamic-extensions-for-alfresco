package nl.runnable.alfresco.webscripts;

import nl.runnable.alfresco.spring.Spied;
import nl.runnable.alfresco.webscripts.annotations.FileField;
import nl.runnable.alfresco.webscripts.annotations.Uri;

import org.springframework.extensions.webscripts.servlet.FormData.FormField;
import org.springframework.stereotype.Component;

@Component
@Spied
public class FileFieldHandler {

	@Uri("/handleFileField")
	public void handleFileField(@FileField final FormField file) {
	}
}
