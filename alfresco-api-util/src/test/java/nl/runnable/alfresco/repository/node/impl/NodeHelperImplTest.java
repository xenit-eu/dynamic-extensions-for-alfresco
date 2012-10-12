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

package nl.runnable.alfresco.repository.node.impl;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;

import nl.runnable.alfresco.repository.mock.MockNamespacePrefixResolver;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.Path;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;
import org.junit.Before;
import org.junit.Test;

public class NodeHelperImplTest {

	private static final NodeRef ROOT_NODE_REF = new NodeRef("workspace", "SpacesStore", "1");
	private static final NodeRef COMPANY_HOME_NODE_REF = new NodeRef("workspace", "SpacesStore", "2");
	private static final NodeRef MY_HOME_NODE_REF = new NodeRef("workspace", "SpacesStore", "3");
	private static final NodeRef MY_FOLDER_NODE_REF = new NodeRef("workspace", "SpacesStore", "4");

	private NodeHelperImpl nodeHelper;

	@Before
	public void setup() {
		final NamespacePrefixResolver namespacePrefixResolver = MockNamespacePrefixResolver
				.createWithCommonNamespaces();

		nodeHelper = new NodeHelperImpl();
		nodeHelper.setNodeService(createMockNodeService(namespacePrefixResolver));
		nodeHelper.setPathHelper(createPathHelper(namespacePrefixResolver));
		nodeHelper.setDictionaryService(createMockDictionaryService());
	}

	@SuppressWarnings("serial")
	private NodeService createMockNodeService(final NamespacePrefixResolver namespacePrefixResolver) {
		final NodeService nodeService = mock(NodeService.class);
		final Path path = new Path();
		path.append(new Path.ChildAssocElement(new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, ROOT_NODE_REF,
				QName.createQName("app:companyhome", namespacePrefixResolver), COMPANY_HOME_NODE_REF, true, 0)));
		path.append(new Path.ChildAssocElement(new ChildAssociationRef(ContentModel.ASSOC_CONTAINS,
				COMPANY_HOME_NODE_REF, QName.createQName("cm:myhome", namespacePrefixResolver), MY_HOME_NODE_REF, true,
				0)));
		path.append(new Path.ChildAssocElement(new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, MY_HOME_NODE_REF,
				QName.createQName("cm:myfolder", namespacePrefixResolver), MY_FOLDER_NODE_REF, true, 0)));
		when(nodeService.getPath(eq(MY_FOLDER_NODE_REF))).thenReturn(path);

		when(nodeService.getProperty(ROOT_NODE_REF, ContentModel.PROP_CATEGORIES)).thenReturn(new ArrayList<String>() {
			{
				add("category1");
				add("category2");
			}
		});
		when(nodeService.getProperty(ROOT_NODE_REF, ContentModel.PROP_NAME)).thenReturn("name");
		when(nodeService.getProperty(ROOT_NODE_REF, ContentModel.PROP_AUTHOR)).thenReturn(null);
		when(nodeService.getType(eq(ROOT_NODE_REF))).thenReturn(ContentModel.TYPE_FOLDER);
		when(nodeService.getPrimaryParent(MY_FOLDER_NODE_REF)).thenReturn(
				new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, MY_HOME_NODE_REF, null, MY_FOLDER_NODE_REF));

		return nodeService;
	}

	private PathHelperImpl createPathHelper(final NamespacePrefixResolver namespacePrefixResolver) {
		final PathHelperImpl pathHelper = new PathHelperImpl();
		pathHelper.setPathEncoder(new SimplePathEncoder());
		pathHelper.setNamespacePrefixResolver(namespacePrefixResolver);
		return pathHelper;
	}

	private DictionaryService createMockDictionaryService() {
		final DictionaryService dictionaryService = mock(DictionaryService.class);
		when(dictionaryService.isSubClass(any(QName.class), eq(ContentModel.TYPE_CMOBJECT))).thenReturn(true);
		return dictionaryService;
	}

	@Test
	public void testGetPath() {
		assertEquals("/app:companyhome/cm:myhome/cm:myfolder", nodeHelper.getPath(MY_FOLDER_NODE_REF));
	}

	@Test
	public void testGetPropertyValuesWithMultipleValues() {
		assertEquals(2, nodeHelper.getPropertyValues(ROOT_NODE_REF, ContentModel.PROP_CATEGORIES).size());
	}

	@Test
	public void testGetPropertyValuesWithSingleValue() {
		assertEquals(1, nodeHelper.getPropertyValues(ROOT_NODE_REF, ContentModel.PROP_NAME).size());
	}

	@Test
	public void testGetPropertyValuesWithNull() {
		assertNull(nodeHelper.getPropertyValues(ROOT_NODE_REF, ContentModel.PROP_AUTHOR));
	}

	@Test
	public void testIsOfType() {
		assertTrue(nodeHelper.isOfType(ROOT_NODE_REF, ContentModel.TYPE_CMOBJECT));
		assertFalse(nodeHelper.isOfType(ROOT_NODE_REF, ContentModel.TYPE_CONTENT));
	}

	@Test
	public void testGetPrimaryParent() {
		assertEquals(MY_HOME_NODE_REF, nodeHelper.getPrimaryParent(MY_FOLDER_NODE_REF));
	}
}
