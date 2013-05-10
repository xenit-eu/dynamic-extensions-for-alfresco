package nl.runnable.alfresco.webscripts;

import javax.annotation.ManagedBean;

import nl.runnable.alfresco.webscripts.annotations.Uri;
import nl.runnable.alfresco.webscripts.annotations.WebScript;

import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

@ManagedBean
@WebScript
public class ArgumentResolverHandler {

	@Uri("/handleWebScriptRequest")
	public void handleWebScriptRequest(final WebScriptRequest request) {
	}

	@Uri("/handleWebScriptResponse")
	public void handleWebScriptResponse(final WebScriptResponse response) {
	}
}
