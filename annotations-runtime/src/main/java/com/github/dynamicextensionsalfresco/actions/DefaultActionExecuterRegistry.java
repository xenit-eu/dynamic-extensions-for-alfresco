package com.github.dynamicextensionsalfresco.actions;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.alfresco.repo.action.executer.ActionExecuter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class DefaultActionExecuterRegistry implements ActionExecuterRegistry, ApplicationContextAware {

	/* Dependencies */

	private ApplicationContext applicationContext;

	/* State */

	private final Map<String, ActionExecuter> actionExecutersByName = new ConcurrentHashMap<String, ActionExecuter>();

	/* Operations */

	@Override
	public boolean hasActionExecuter(final String name) {
		/* Note: we must verify the ApplicationContext for matching bean names as well. */
		return actionExecutersByName.containsKey(name) || applicationContext.containsBeanDefinition(name);
	}

	@Override
	public ActionExecuter getActionExecuter(final String name) {
		return actionExecutersByName.get(name);
	}

	@Override
	public void registerActionExecuter(final ActionExecuter actionExecuter) {
		final String name = actionExecuter.getActionDefinition().getName();
		if (hasActionExecuter(name)) {
			throw new IllegalStateException("Duplicate ActionExecuter " + name);
		}
		getActionExecutersByName().put(name, actionExecuter);
	}

	@Override
	public void unregisterActionExecuter(final ActionExecuter actionExecuter) {
		if (getActionExecutersByName().containsValue(actionExecuter)) {
			actionExecutersByName.remove(actionExecuter.getActionDefinition().getName());
		}
	}

	/* Dependencies */

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	/* State */

	protected Map<String, ActionExecuter> getActionExecutersByName() {
		return actionExecutersByName;
	}

}
