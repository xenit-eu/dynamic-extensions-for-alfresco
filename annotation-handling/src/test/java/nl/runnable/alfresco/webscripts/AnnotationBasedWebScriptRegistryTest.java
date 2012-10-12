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

package nl.runnable.alfresco.webscripts;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.extensions.webscripts.Description;
import org.springframework.extensions.webscripts.Description.RequiredAuthentication;
import org.springframework.extensions.webscripts.Description.RequiredTransaction;
import org.springframework.extensions.webscripts.Description.TransactionCapability;
import org.springframework.extensions.webscripts.WebScript;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class AnnotationBasedWebScriptRegistryTest {

	private AnnotationBasedWebScriptRegistry registry;

	@Autowired
	public void setRegistry(final AnnotationBasedWebScriptRegistry registry) {
		this.registry = registry;
	}

	@Test
	public void testGetWebScripts() {
		final List<WebScript> webScripts = new ArrayList<WebScript>(registry.getWebScripts());
		assertEquals(2, webScripts.size());
		final List<String> methods = Arrays.asList("GET", "POST");
		final List<String> handlerMethods = Arrays.asList("handleGetRequest", "handlePostRequest");
		final List<String[]> uris = Arrays.asList(new String[] { "/path/to/resource/{id}" },
				new String[] { "/path/to/post" });
		for (int i = 0; i < webScripts.size(); i++) {
			final WebScript webScript = webScripts.get(i);
			final Description description = webScript.getDescription();
			assertEquals(methods.get(i), description.getMethod());
			assertArrayEquals(uris.get(i), description.getURIs());
			assertEquals("nl.runnable.alfresco.webscripts.ExampleWebScript." + handlerMethods.get(i) + "."
					+ methods.get(i).toLowerCase(), description.getId());

			assertEquals("ExampleWebScript", description.getShortName());
			assertEquals("Example web script used for test cases.", description.getDescription());
			assertEquals("admin", description.getRunAs());
			assertEquals(RequiredAuthentication.user, description.getRequiredAuthentication());
			assertEquals(RequiredTransaction.requiresnew, description.getRequiredTransaction());
			assertEquals(TransactionCapability.readonly, description.getRequiredTransactionParameters().getCapability());
		}
	}

}
