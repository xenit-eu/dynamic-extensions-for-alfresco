package nl.runnable.alfresco.webscripts;

import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * Maps {@link WebScriptRequest} method parameters.
 * 
 * @author Laurens Fridael
 * 
 */
public class WebScriptArgumentResolver extends
		AbstractTypeBasedArgumentResolver<org.springframework.extensions.webscripts.WebScript> {

	@Override
	protected Class<?> getExpectedArgumentType() {
		return org.springframework.extensions.webscripts.WebScript.class;
	}

	@Override
	protected org.springframework.extensions.webscripts.WebScript resolveArgument(final WebScriptRequest request,
			final WebScriptResponse response) {
		return request.getServiceMatch().getWebScript();
	}

}
