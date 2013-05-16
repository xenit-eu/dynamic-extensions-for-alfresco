package nl.runnable.alfresco.webscripts;

import javax.annotation.ManagedBean;

import nl.runnable.alfresco.webscripts.annotations.FileField;
import nl.runnable.alfresco.webscripts.annotations.Uri;
import nl.runnable.alfresco.webscripts.annotations.WebScript;

import org.springframework.extensions.webscripts.servlet.FormData.FormField;

@ManagedBean
@WebScript
public class FileFieldHandler {

	@Uri("/handleFileField")
	public void handleFileField(@FileField final FormField file) {
	}
}
