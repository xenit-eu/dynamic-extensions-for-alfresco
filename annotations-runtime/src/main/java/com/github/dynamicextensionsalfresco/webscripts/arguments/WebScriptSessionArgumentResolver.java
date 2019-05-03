package com.github.dynamicextensionsalfresco.webscripts.arguments;


import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.extensions.webscripts.WebScriptSession;

public class WebScriptSessionArgumentResolver extends AbstractTypeBasedArgumentResolver<WebScriptSession> {

	@Override
	protected Class<?> getExpectedArgumentType() {
		return WebScriptSession.class;
	}

	@Override
	protected WebScriptSession resolveArgument(final WebScriptRequest request, final WebScriptResponse response) {
		return request.getRuntime().getSession();
	}

}
