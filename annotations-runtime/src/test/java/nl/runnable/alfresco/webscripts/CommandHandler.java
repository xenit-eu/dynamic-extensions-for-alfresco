package nl.runnable.alfresco.webscripts;

import nl.runnable.alfresco.webscripts.annotations.Command;
import nl.runnable.alfresco.webscripts.annotations.Uri;
import nl.runnable.alfresco.webscripts.annotations.WebScript;

import org.springframework.stereotype.Component;

@Component
@WebScript
public class CommandHandler {

	@Uri("/handleCommand")
	public void handleCommand(@Command final Person command) {
	}
}
