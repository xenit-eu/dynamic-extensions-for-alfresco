package nl.runnable.alfresco.examples;

import java.io.IOException;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import nl.runnable.alfresco.webscripts.annotations.RequestParam;
import nl.runnable.alfresco.webscripts.annotations.Uri;
import nl.runnable.alfresco.webscripts.annotations.WebScript;

import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * This Web Script executes the Action named {@link ExampleActions#SET_DESCRIPTION_ACTION}, which maps to the
 * annotation-based Action {@link ExampleActions#setDescription(NodeRef, String)}.
 * 
 * @author Laurens Fridael
 * 
 */
@ManagedBean
@WebScript
public class SetDescriptionWebScript {

	@Inject
	private ActionService actionService;

	@Uri("/dynamic-extensions/examples/set-description")
	public void executeSetDescriptionAction(final @RequestParam(value = "nodeRef") NodeRef nodeRef,
			@RequestParam final String description, final WebScriptResponse response) throws IOException {
		final Action action = actionService.createAction(ExampleActions.SET_DESCRIPTION_ACTION);
		action.setParameterValue(ExampleActions.DESCRIPTION_PARAM, description);
		actionService.executeAction(action, nodeRef);
		response.getWriter().write(
				String.format("Executed action %s on %s", ExampleActions.SET_DESCRIPTION_ACTION, nodeRef));
	}

}
