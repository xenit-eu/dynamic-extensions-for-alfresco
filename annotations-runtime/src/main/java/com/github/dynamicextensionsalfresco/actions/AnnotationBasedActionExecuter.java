package com.github.dynamicextensionsalfresco.actions;

import org.alfresco.repo.action.executer.ActionExecuter;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionDefinition;
import org.alfresco.service.cmr.repository.NodeRef;

class AnnotationBasedActionExecuter implements ActionExecuter {

	private final ActionMethodMapping mapping;

	private final ActionDefinition actionDefinition;

	private final String queueName;

	AnnotationBasedActionExecuter(final ActionDefinition actionDefinition, final ActionMethodMapping mapping,
			final String queueName) {
		this.actionDefinition = actionDefinition;
		this.mapping = mapping;
		this.queueName = queueName;
	}

	@Override
	public ActionDefinition getActionDefinition() {
		return actionDefinition;
	}

	@Override
	public String getQueueName() {
		return queueName;
	}

	@Override
	public void execute(final Action action, final NodeRef actionedUponNodeRef) {
		try {
			mapping.invokeActionMethod(action, actionedUponNodeRef);
		} catch (final RuntimeException e) {
			throw e;
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	/** Alfresco 4.0 */

	@Override
	public boolean getIgnoreLock() {
		return false;
	}

	@Override
	public boolean getTrackStatus() {
		return false;
	}

}
