package com.github.dynamicextensionsalfresco.actions;

import org.alfresco.repo.action.executer.ActionExecuter;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionDefinition;
import org.alfresco.service.cmr.repository.NodeRef;

/**
 * Dummy action definition we can use to populate the action list.
 * 
 * @author Laurent Van der Linden
 */
class DummyActionExecutor implements ActionExecuter {
	private final ActionDefinition actionDefinition;

	DummyActionExecutor(final ActionDefinition actionDefinition) {
		this.actionDefinition = actionDefinition;
	}

	@Override
	public String getQueueName() {
		return null;
	}

	@Override
	public boolean getIgnoreLock() {
		return false;
	}

	@Override
	public boolean getTrackStatus() {
		return false;
	}

	@Override
	public ActionDefinition getActionDefinition() {
		return actionDefinition;
	}

	@Override
	public void execute(final Action action, final NodeRef actionedUponNodeRef) {
		throw new IllegalStateException("this action implementation has been uninstalled");
	}
}
