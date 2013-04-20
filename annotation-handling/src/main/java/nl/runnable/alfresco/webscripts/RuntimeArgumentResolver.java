package nl.runnable.alfresco.webscripts;

import org.springframework.extensions.webscripts.Runtime;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

public class RuntimeArgumentResolver extends AbstractTypeBasedArgumentResolver<Runtime> {

	@Override
	protected Class<?> getExpectedArgumentType() {
		return Runtime.class;
	}

	@Override
	protected Runtime resolveArgument(final WebScriptRequest request, final WebScriptResponse response) {
		return request.getRuntime();
	}

}
