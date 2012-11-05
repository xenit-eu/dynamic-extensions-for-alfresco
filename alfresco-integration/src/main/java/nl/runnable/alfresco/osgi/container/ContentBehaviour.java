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

import nl.runnable.alfresco.osgi.BundleModel;
import nl.runnable.alfresco.repository.node.NodeHelper;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.ContentServicePolicies;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

/**
 * Handles updates of {@link ContentModel#TYPE_CONTENT} nodes within folders of type
 * {@link BundleModel#TYPE_BUNDLE_FOLDER}. This behaviour accommodates uploads of Bundle resources through FTP.
 * 
 * @author Laurens Fridael
 * @deprecated Repository bundle management will be removed in the future.
 */
@Deprecated
public class ContentBehaviour extends AbstractBundleBehaviour implements ContentServicePolicies.OnContentUpdatePolicy {

	/* Dependencies */

	private NodeService nodeService;

	private NodeHelper nodeHelper;

	private BundleHelper bundleHelper;

	/* Operations */

	@Override
	public void register() {
		getPolicyComponent().bindClassBehaviour(ContentServicePolicies.OnContentUpdatePolicy.QNAME,
				ContentModel.TYPE_CONTENT,
				new JavaBehaviour(this, "onContentUpdate", NotificationFrequency.TRANSACTION_COMMIT));
	}

	@Override
	public void onContentUpdate(final NodeRef nodeRef, final boolean newContent) {
		if (newContent) {
			final NodeHelper nodeHelper = getNodeHelper();
			if (getNodeService().exists(nodeRef) && nodeHelper.isOfType(nodeRef, BundleModel.TYPE_BUNDLE) == false) {
				if (nodeHelper.isOfType(nodeHelper.getPrimaryParent(nodeRef), BundleModel.TYPE_BUNDLE_FOLDER)) {
					final BundleService bundleService = getBundleService();
					final ManagedBundle bundleInfo = bundleService.installBundle(nodeRef);
					if (bundleInfo != null && bundleService.startBundleAutomatically(bundleInfo.getNodeRef())) {
						getBundleHelper().startBundle(bundleInfo.getBundle());
					}
				}
			}
		}
	}

	/* Dependencies */

	@Required
	public void setNodeService(final NodeService nodeService) {
		this.nodeService = nodeService;
	}

	protected NodeService getNodeService() {
		return nodeService;
	}

	@Required
	public void setNodeHelper(final NodeHelper nodeHelper) {
		Assert.notNull(nodeHelper);
		this.nodeHelper = nodeHelper;
	}

	protected NodeHelper getNodeHelper() {
		return nodeHelper;
	}

	public void setBundleHelper(final BundleHelper bundleHelper) {
		this.bundleHelper = bundleHelper;
	}

	protected BundleHelper getBundleHelper() {
		return bundleHelper;
	}

}
