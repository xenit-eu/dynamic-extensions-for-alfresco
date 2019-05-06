package com.github.dynamicextensionsalfresco.actions;

import org.alfresco.repo.action.executer.ActionExecuter;
import org.alfresco.service.cmr.action.Action;

/**
 * Defines operations for registering {@link ActionExecuter}s.
 * <p>
 * In practice, this is the global registration point for annotation-based {@link Action}s. Every Dynamic Extension
 * registers and unregister its {@link ActionExecuter}s with a singleton instance.
 * 
 * @author Laurens Fridael
 * 
 */
public interface ActionExecuterRegistry {

	boolean hasActionExecuter(final String name);

	void registerActionExecuter(final ActionExecuter actionExecuter);

	ActionExecuter getActionExecuter(final String name);

	void unregisterActionExecuter(final ActionExecuter actionExecuter);

}