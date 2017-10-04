package com.github.dynamicextensionsalfresco.webscripts;

import org.springframework.extensions.webscripts.*;

import java.io.IOException;
import java.util.ResourceBundle;

class ExampleWebScript implements WebScript {

	/* State */

	private final int status;

	private final DescriptionImpl description;

	ExampleWebScript(final int status, final String... uris) {
		this.status = status;
		description = new DescriptionImpl();
		description.setMethod("GET");
		description.setUris(uris);
		description.setId(getClass().getName());
	}

	/* Main operations */

	@Override
	public void execute(final WebScriptRequest request, final WebScriptResponse response) throws IOException {
		response.setStatus(status);
	}

	@Override
	public Description getDescription() {
		return description;
	}

	/* Remaining operations */

	@Override
	public void init(final Container container, final Description description) {
	}

	@Override
	public ResourceBundle getResources() {
		return null;
	}

	@Override
	public void setURLModelFactory(final URLModelFactory urlModelFactory) {
	}

}
