package com.github.dynamicextensionsalfresco.actions;

import com.github.dynamicextensionsalfresco.actions.annotations.ActionMethod;
import com.github.dynamicextensionsalfresco.actions.annotations.ActionParam;

/**
 * Sample action for testing.
 * 
 * @author Laurent Van der Linden
 */
public class ExampleAction {
	@ActionMethod(value = "exampleActionWithoutConstraint", queueName = "exampleQueue", adhocPropertiesAllowed = true, applicableTypes = {
			"cm:content", "cm:folder" })
	public void withoutConstraint(@ActionParam(value = "name", mandatory = true) final String name) {
	}

	@ActionMethod(value = "exampleActionWithConstraint", queueName = "exampleQueue", adhocPropertiesAllowed = true, applicableTypes = {
			"cm:content", "cm:folder" })
	public void withConstraint(
			@ActionParam(value = "name", mandatory = true, constraintName = "required") final String name) {
	}
}
