/*
Copyright (c) 2012, Runnable
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
 * Neither the name of Runnable nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package nl.runnable.alfresco.actions;

import org.alfresco.repo.action.executer.ActionExecuter;
import org.alfresco.service.cmr.action.ActionDefinition;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.namespace.QName;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class ActionExecuterRegistryTest {

	private ActionExecuterRegistry actionExecuterRegistry;

	@Autowired
	public void setActionExecuterRegistry(ActionExecuterRegistry actionExecuterRegistry) {
		this.actionExecuterRegistry = actionExecuterRegistry;
	}

	@Test
	public void testAnnotatedActions() {
		validateMetadata(actionExecuterRegistry.getActionExecuter("exampleActionWithoutConstraint"), false);

		validateMetadata(actionExecuterRegistry.getActionExecuter("exampleActionWithConstraint"), true);
	}

	private void validateMetadata(ActionExecuter exampleAction, boolean withConstraint) {
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
			assertEquals("constraint should be either null or a non-empty value", null, nameParameter.getParameterConstraintName());
		}
	}
}
