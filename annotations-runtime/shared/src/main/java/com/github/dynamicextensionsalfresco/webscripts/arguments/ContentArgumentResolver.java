package com.github.dynamicextensionsalfresco.webscripts.arguments;


import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * {@link ArgumentResolver} for {@link Content} from {@link WebScriptRequest}s.
 * 
 * @author Laurens Fridael
 * 
 */
public class ContentArgumentResolver extends AbstractTypeBasedArgumentResolver<Content> {

	@Override
	protected Class<?> getExpectedArgumentType() {
		return Content.class;
	}

	@Override
	protected Content resolveArgument(final WebScriptRequest request, final WebScriptResponse response) {
		return request.getContent();
	}

}
