package nl.runnable.alfresco.webscripts;

import nl.runnable.alfresco.spring.Spied;
import nl.runnable.alfresco.webscripts.annotations.Command;
import nl.runnable.alfresco.webscripts.annotations.Uri;

import org.springframework.stereotype.Component;

@Component
@Spied
public class CommandHandler {

	@Uri("/handleCommand")
	public void handleCommand(@Command final Person command) {
	}
}
