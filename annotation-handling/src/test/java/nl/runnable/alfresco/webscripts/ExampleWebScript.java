package nl.runnable.alfresco.webscripts;

import nl.runnable.alfresco.webscripts.annotations.Authentication;
import nl.runnable.alfresco.webscripts.annotations.AuthenticationType;
import nl.runnable.alfresco.webscripts.annotations.HttpMethod;
import nl.runnable.alfresco.webscripts.annotations.Transaction;
import nl.runnable.alfresco.webscripts.annotations.TransactionType;
import nl.runnable.alfresco.webscripts.annotations.Uri;
import nl.runnable.alfresco.webscripts.annotations.UriVariable;
import nl.runnable.alfresco.webscripts.annotations.WebScript;

import org.springframework.stereotype.Component;

/**
 * Example annotation-based Web Script for integration tests.
 * 
 * @author Laurens Fridael
 * 
 */
@Component
@WebScript(description = "Example web script used for test cases.")
@Authentication(value = AuthenticationType.USER, runAs = "admin")
@Transaction(value = TransactionType.REQUIRES_NEW, readOnly = true)
public class ExampleWebScript {

	@Uri(value = "/path/to/resource/{id}", method = HttpMethod.GET)
	public void handleGetRequest(@UriVariable("id") final String id) {
	}

	@Uri(value = "/path/to/post", method = HttpMethod.POST)
	public void handlePostRequest() {
	}
}
