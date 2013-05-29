package nl.runnable.alfresco.webscripts;

import nl.runnable.alfresco.spring.Spied;
import nl.runnable.alfresco.webscripts.annotations.Uri;
import nl.runnable.alfresco.webscripts.annotations.UriVariable;

import org.springframework.stereotype.Component;

@Component
@Spied
public class UriVariableHandler {

	String variable;

	@Uri("/handleUriVariable/{variable}")
	public void handleUriVariable(@UriVariable final String variable) {
		this.variable = variable;

	}

}
