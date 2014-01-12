package com.github.dynamicextensionsalfresco.actions;

import com.github.dynamicextensionsalfresco.actions.annotations.ActionMethod;

import org.alfresco.service.cmr.action.ParameterDefinition;

/**
 * Represents a mapping of an Action parameter to a parameter of an {@link ActionMethod}-annotated method.
 * 
 * @author Laurens Fridael
 * 
 */
class ParameterMapping {

	private final String name;

	private final int index;

	private final Class<?> expectedType;

	private final boolean multivalued;

	private final boolean mandatory;

	ParameterMapping(final ParameterDefinition parameterDefinition, final int index, final Class<?> expectedType) {
		this.name = parameterDefinition.getName();
		this.index = index;
		this.expectedType = expectedType;
		this.multivalued = parameterDefinition.isMultiValued();
		this.mandatory = parameterDefinition.isMandatory();
	}

	public String getName() {
		return name;
	}

	public int getIndex() {
		return index;
	}

	public Class<?> getExpectedType() {
		return expectedType;
	}

	public boolean isMultivalued() {
		return multivalued;
	}

	public boolean isMandatory() {
		return mandatory;
	}

}
