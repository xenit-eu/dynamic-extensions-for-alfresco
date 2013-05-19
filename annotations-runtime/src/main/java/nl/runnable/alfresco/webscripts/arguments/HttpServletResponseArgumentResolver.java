package nl.runnable.alfresco.webscripts.arguments;

import javax.servlet.http.HttpServletResponse;

import nl.runnable.alfresco.webscripts.WebScriptUtil;

import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

public class HttpServletResponseArgumentResolver extends AbstractTypeBasedArgumentResolver<HttpServletResponse> {

	@Override
	protected Class<?> getExpectedArgumentType() {
		return HttpServletResponse.class;
	}

	@Override
	protected HttpServletResponse resolveArgument(final WebScriptRequest request, final WebScriptResponse response) {
		final HttpServletResponse httpServletResponse = WebScriptUtil.extractHttpServletResponse(response);
		if (httpServletResponse == null) {
			throw new RuntimeException("Cannot extract HttpServletResponse from WebScriptResponse.");
		}
		return httpServletResponse;
	}

}
