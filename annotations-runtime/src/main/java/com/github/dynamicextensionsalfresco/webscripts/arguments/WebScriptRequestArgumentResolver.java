package com.github.dynamicextensionsalfresco.webscripts.arguments;


import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * Maps {@link WebScriptRequest} method parameters.
 * 
 * @author Laurens Fridael
 * 
 */
public class WebScriptRequestArgumentResolver extends AbstractTypeBasedArgumentResolver<WebScriptRequest> {

	@Override
	protected Class<?> getExpectedArgumentType() {
		return WebScriptRequest.class;
	}

	@Override
	protected WebScriptRequest resolveArgument(final WebScriptRequest request, final WebScriptResponse response) {
		return request;
	}

}
