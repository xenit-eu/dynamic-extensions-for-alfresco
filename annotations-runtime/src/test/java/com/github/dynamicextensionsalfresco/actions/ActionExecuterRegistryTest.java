package com.github.dynamicextensionsalfresco.actions;

import static org.junit.Assert.*;

import java.util.List;

import org.alfresco.repo.action.executer.ActionExecuter;
import org.alfresco.service.cmr.action.ActionDefinition;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.namespace.QName;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class ActionExecuterRegistryTest {

	private ActionExecuterRegistry actionExecuterRegistry;

	@Autowired
	public void setActionExecuterRegistry(final ActionExecuterRegistry actionExecuterRegistry) {
		this.actionExecuterRegistry = actionExecuterRegistry;
	}

	@Test
	public void testAnnotatedActions() {
		validateMetadata(actionExecuterRegistry.getActionExecuter("exampleActionWithoutConstraint"), false);

		validateMetadata(actionExecuterRegistry.getActionExecuter("exampleActionWithConstraint"), true);
	}

	private void validateMetadata(final ActionExecuter exampleAction, final boolean withConstraint) {
		assertNotNull(exampleAction);
		final ActionDefinition actionDefinition = exampleAction.getActionDefinition();
		final List<QName> applicableTypes = actionDefinition.getApplicableTypes();
		assertEquals(2, applicableTypes.size());

		assertEquals(true, actionDefinition.getAdhocPropertiesAllowed());

		final List<ParameterDefinition> parameterDefinitions = actionDefinition.getParameterDefinitions();
		assertEquals(1, parameterDefinitions.size());
		final ParameterDefinition nameParameter = parameterDefinitions.get(0);
		assertEquals("name", nameParameter.getName());

		if (withConstraint) {
			assertEquals("required", nameParameter.getParameterConstraintName());
		} else {
			assertEquals("constraint should be either null or a non-empty value", null,
					nameParameter.getParameterConstraintName());
		}
	}
}
