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

package nl.runnable.alfresco.osgi.container;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nl.runnable.alfresco.osgi.BundleModel;
import nl.runnable.alfresco.repository.node.NodeHelper;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.Path;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.RegexQNamePattern;

/**
 * {@link ManagedBundleLocator} implementation that relies only on {@link NodeService}.
 * <p>
 * This implementation is primarily useful in situations where a {@link SearchService} is not (yet) available. This
 * happens in Alfresco 4.0 during module startup, with Solr is configured as the indexing engine.
 * 
 * @author Laurens Fridael
 * @deprecated Repository bundle management will be removed in the future.
 */
@Deprecated
public class NodeServiceManagedBundleLocator implements ManagedBundleLocator {

	/* Dependencies */

	private NodeService nodeService;

	private NodeHelper nodeHelper;

	/* Operations */

	@Override
	public List<NodeRef> getExtensionBundles() {
		List<NodeRef> bundles = Collections.emptyList();
		final NodeRef folder = getExtensionBundlesFolder();
		if (folder != null) {
			bundles = getManagedBundles(folder);
		}
		return bundles;
	}

	@Override
	public List<NodeRef> getLibraryBundles() {
		List<NodeRef> bundles = Collections.emptyList();
		final NodeRef folder = getLibraryBundlesFolder();
		if (folder != null) {
			bundles = getManagedBundles(folder);
		}
		return bundles;
	}

	protected List<NodeRef> getManagedBundles(final NodeRef folder) {
		final List<NodeRef> managedBundles = new ArrayList<NodeRef>();
		final List<ChildAssociationRef> childAssocs = getNodeService().getChildAssocs(folder,
				ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL);
		for (final ChildAssociationRef childAssociationRef : childAssocs) {
			final NodeRef childRef = childAssociationRef.getChildRef();
			if (getNodeHelper().isOfType(childRef, BundleModel.TYPE_MANAGED_BUNDLE)) {
				managedBundles.add(childRef);
			}
		}
		return managedBundles;
	}

	protected NodeRef getExtensionBundlesFolder() {
		NodeRef folder = null;
		final NodeRef dynamicExtensionsFolder = getDynamicExtensionsFolder();
		if (dynamicExtensionsFolder != null) {
			folder = getNodeService().getChildByName(dynamicExtensionsFolder, ContentModel.ASSOC_CONTAINS,
					"Extension Bundles");
		}
		return folder;
	}

	protected NodeRef getLibraryBundlesFolder() {
		NodeRef folder = null;
		final NodeRef dynamicExtensionsFolder = getDynamicExtensionsFolder();
		if (dynamicExtensionsFolder != null) {
			folder = getNodeService().getChildByName(dynamicExtensionsFolder, ContentModel.ASSOC_CONTAINS,
					"Library Bundles");
		}
		return folder;
	}

	protected NodeRef getDynamicExtensionsFolder() {
		NodeRef folder = null;
		final NodeRef dataDictionaryFolder = getDataDictionaryFolder();
		if (dataDictionaryFolder != null) {
			folder = getNodeService().getChildByName(dataDictionaryFolder, ContentModel.ASSOC_CONTAINS,
					"Dynamic Extensions");
		}
		return folder;
	}

	protected NodeRef getDataDictionaryFolder() {
		return getNodeService().getChildByName(getCompanyHome(), ContentModel.ASSOC_CONTAINS, "Data Dictionary");
	}

	protected NodeRef getCompanyHome() {
		NodeRef companyHome = null;
		final NodeRef rootNode = getNodeService().getRootNode(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		for (final ChildAssociationRef childAssociationRef : getNodeService().getChildAssocs(rootNode,
				ContentModel.ASSOC_CHILDREN, RegexQNamePattern.MATCH_ALL)) {
			final NodeRef childRef = childAssociationRef.getChildRef();
			final Path path = getNodeService().getPath(childRef);
			if (path.size() > 1
					&& "{http://www.alfresco.org/model/application/1.0}company_home".equals(path.get(1)
							.getElementString())) {
				companyHome = childRef;
				break;
			}
		}
		return companyHome;
	}

	/* Dependencies */

	public void setNodeService(final NodeService nodeService) {
		this.nodeService = nodeService;
	}

	protected NodeService getNodeService() {
		return nodeService;
	}

	public void setNodeHelper(final NodeHelper nodeHelper) {
		this.nodeHelper = nodeHelper;
	}

	protected NodeHelper getNodeHelper() {
		return nodeHelper;
	}

}
