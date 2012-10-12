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

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import nl.runnable.alfresco.repository.node.NodeHelper;
import nl.runnable.alfresco.repository.node.PathHelper;

import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.Path;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

public class NodeHelperImpl implements NodeHelper {

	private NodeService nodeService;

	private SearchService searchService;

	private NamespaceService namespaceService;

	private DictionaryService dictionaryService;

	private PathHelper pathHelper;

	private NodeRef companyHomeNodeRef;

	@Required
	public void setNodeService(final NodeService nodeService) {
		Assert.notNull(nodeService, "NodeService cannot be null.");
		this.nodeService = nodeService;
	}

	protected NodeService getNodeService() {
		return nodeService;
	}

	@Required
	public void setSearchService(final SearchService searchService) {
		Assert.notNull(searchService);
		this.searchService = searchService;
	}

	protected SearchService getSearchService() {
		return searchService;
	}

	@Required
	public void setNamespaceService(final NamespaceService namespaceService) {
		Assert.notNull(namespaceService);
		this.namespaceService = namespaceService;
	}

	protected NamespaceService getNamespaceService() {
		return namespaceService;
	}

	@Required
	public void setDictionaryService(final DictionaryService dictionaryService) {
		Assert.notNull(dictionaryService, "DictionaryService cannot be null.");
		this.dictionaryService = dictionaryService;
	}

	protected DictionaryService getDictionaryService() {
		return dictionaryService;
	}

	@Required
	public void setPathHelper(final PathHelper pathHelper) {
		Assert.notNull(pathHelper, "PathHelper cannot be null.");
		this.pathHelper = pathHelper;
	}

	protected PathHelper getPathHelper() {
		return pathHelper;
	}

	@Override
	public String getPath(final NodeRef nodeRef) {
		Assert.notNull(nodeRef, "NodeRef cannot be null.");
		final Path path = getNodeService().getPath(nodeRef);
		return getPathHelper().convertPathToString(path);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Serializable> Collection<T> getPropertyValues(final NodeRef nodeRef, final QName property) {
		Assert.notNull(nodeRef, "NodeRef cannot be null.");
		Assert.notNull(property, "Property cannot be null.");
		final Object value = getNodeService().getProperty(nodeRef, property);
		if (value instanceof Collection) {
			return (Collection<T>) value;
		} else if (value != null) {
			return Arrays.asList((T) value);
		} else {
			return null;
		}
	}

	@Override
	public boolean isOfType(final NodeRef nodeRef, final QName type) {
		Assert.notNull(nodeRef, "NodeRef cannot be null.");
		Assert.notNull(type, "Type cannot be null.");
		return getDictionaryService().isSubClass(getNodeService().getType(nodeRef), type);
	}

	@Override
	public NodeRef getPrimaryParent(final NodeRef nodeRef) {
		Assert.notNull(nodeRef, "NodeRef cannot be null.");
		return getNodeService().getPrimaryParent(nodeRef).getParentRef();
	}

	@Override
	public NodeRef getCompanyHome() {
		if (companyHomeNodeRef == null) {
			final NodeRef rootNode = getNodeService().getRootNode(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
			final List<NodeRef> results = searchService.selectNodes(rootNode, "/app:company_home", null,
					getNamespaceService(), false);
			if (results.size() == 1) {
				companyHomeNodeRef = results.get(0);
			}
		}
		return companyHomeNodeRef;
	}
}
