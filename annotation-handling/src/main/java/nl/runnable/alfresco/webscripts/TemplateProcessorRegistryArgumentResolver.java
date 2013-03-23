package nl.runnable.alfresco.webscripts;

import org.springframework.extensions.webscripts.TemplateProcessorRegistry;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * Resolves {@link TemplateProcessorRegistry} arguments for Web Script handler methods.
 * 
 * @author Laurens Fridael
 * 
 */
public class TemplateProcessorRegistryArgumentResolver extends
		AbstractTypeBasedArgumentResolver<TemplateProcessorRegistry> {

	@Override
	protected Class<?> getExpectedArgumentType() {
		return TemplateProcessorRegistry.class;
	}

	@Override
	protected TemplateProcessorRegistry resolveArgument(final WebScriptRequest request, final WebScriptResponse response) {
		return request.getRuntime().getContainer().getTemplateProcessorRegistry();
	}

}
