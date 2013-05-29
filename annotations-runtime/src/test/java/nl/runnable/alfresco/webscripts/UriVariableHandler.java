package nl.runnable.alfresco.webscripts;

import nl.runnable.alfresco.webscripts.annotations.Uri;
import nl.runnable.alfresco.webscripts.annotations.UriVariable;
import nl.runnable.alfresco.webscripts.annotations.WebScript;

import org.springframework.stereotype.Component;

@Component
@WebScript
public class UriVariableHandler {

	String variable;

	@Uri("/handleUriVariable/{variable}")
	public void handleUriVariable(@UriVariable final String variable) {
		this.variable = variable;

	}

}
