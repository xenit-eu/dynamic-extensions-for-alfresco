package nl.runnable.alfresco.webscripts.arguments;

import javax.servlet.http.HttpServletRequest;

import nl.runnable.alfresco.webscripts.WebScriptUtil;

import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

public class HttpServletRequestArgumentResolver extends AbstractTypeBasedArgumentResolver<HttpServletRequest> {

	@Override
	protected Class<?> getExpectedArgumentType() {
		return HttpServletRequest.class;
	}

	@Override
	protected HttpServletRequest resolveArgument(final WebScriptRequest request, final WebScriptResponse response) {
		final HttpServletRequest httpServletRequest = WebScriptUtil.extractHttpServletRequest(request);
		if (httpServletRequest == null) {
			throw new RuntimeException("Cannot extract HttpServletRequest from WebScriptRequest.");
		}
		return httpServletRequest;
	}

}
