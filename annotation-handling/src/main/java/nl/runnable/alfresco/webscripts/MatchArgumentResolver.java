package nl.runnable.alfresco.webscripts;

import org.springframework.extensions.webscripts.Match;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * {@link ArgumentResolver} for {@link Match} objects.
 * 
 * @author Laurens Fridael
 * 
 */
public class MatchArgumentResolver extends AbstractTypeBasedArgumentResolver<Match> {

	@Override
	protected Class<?> getExpectedArgumentType() {
		return Match.class;
	}

	@Override
	protected Match resolveArgument(final WebScriptRequest request, final WebScriptResponse response) {
		return request.getServiceMatch();
	}

}
