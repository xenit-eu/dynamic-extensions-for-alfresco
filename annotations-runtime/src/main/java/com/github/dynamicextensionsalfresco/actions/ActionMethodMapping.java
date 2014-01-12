package com.github.dynamicextensionsalfresco.actions;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.github.dynamicextensionsalfresco.actions.annotations.ActionMethod;

import org.alfresco.repo.action.executer.ActionExecuter;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.rule.RuleServiceException;

/**
 * Represents a mapping from {@link ActionExecuter#execute(Action, NodeRef)} to an {@link ActionMethod} -annotated
 * method.
 * 
 * @author Laurens Fridael
 * 
 */
class ActionMethodMapping {

	private final Object bean;

	private final Method method;

	private int nodeRefParameterIndex = -1;

	private int actionParameterIndex = -1;

	private final int parameterCount;

	private final Map<String, ParameterMapping> parameterMappingsByName = new HashMap<String, ParameterMapping>();

	ActionMethodMapping(final Object bean, final Method method) {
		this.bean = bean;
		this.method = method;
		this.parameterCount = method.getParameterTypes().length;
	}

	public void invokeActionMethod(final Action action, final NodeRef nodeRef) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		final Object[] parameters = new Object[parameterCount];
		if (nodeRefParameterIndex > -1) {
			parameters[nodeRefParameterIndex] = nodeRef;
		}
		if (actionParameterIndex > -1) {
			parameters[actionParameterIndex] = action;
		}
		for (final Entry<String, ParameterMapping> entry : parameterMappingsByName.entrySet()) {
			final ParameterMapping parameterMapping = entry.getValue();
			Serializable value = action.getParameterValue(parameterMapping.getName());
			if (parameterMapping.isMandatory() && value == null) {
				/*
				 * We throw RuleServiceException just as ParameterizedItemAbstractBase does when it encounters a missing
				 * value for a mandatory property.
				 */
				throw new RuleServiceException(String.format("Parameter '%s' is mandatory, but no value was given.",
						parameterMapping.getName()));
			}
			/* Single values for a multi-valued property are wrapped in an ArrayList automatically. */
			if (parameterMapping.isMultivalued() && (value instanceof Collection) == false) {
				value = new ArrayList<Serializable>(Arrays.asList(value));
			}
			parameters[parameterMapping.getIndex()] = value;
		}
		method.invoke(bean, parameters);
	}

	public void setNodeRefParameterIndex(final int index) {
		this.nodeRefParameterIndex = index;
	}

	public int getNodeRefParameterIndex() {
		return nodeRefParameterIndex;
	}

	public void setActionParameterIndex(final int actionParameterIndex) {
		this.actionParameterIndex = actionParameterIndex;
	}

	public int getActionParameterIndex() {
		return actionParameterIndex;
	}

	public boolean hasParameter(final String name) {
		return parameterMappingsByName.containsKey(name);
	}

	public void addParameterMapping(final ParameterMapping parameterMapping) {
		final String name = parameterMapping.getName();
		if (parameterMappingsByName.containsKey(name) == false) {
			parameterMappingsByName.put(name, parameterMapping);
		} else {
			throw new IllegalStateException(String.format("Duplicate parameter name '%s'.", name));
		}
	}

}
