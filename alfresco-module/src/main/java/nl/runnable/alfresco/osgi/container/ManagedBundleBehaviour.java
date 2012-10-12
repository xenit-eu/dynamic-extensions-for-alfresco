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

import org.alfresco.repo.content.ContentServicePolicies;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.service.cmr.repository.NodeRef;

/**
 * Handles updates of {@link BundleModel#TYPE_MANAGED_BUNDLE} nodes.
 * 
 * @author Laurens Fridael
 * @deprecated Repository bundle management will be removed in the future.
 */
@Deprecated
public class ManagedBundleBehaviour extends AbstractBundleBehaviour implements
		ContentServicePolicies.OnContentUpdatePolicy, NodeServicePolicies.BeforeDeleteNodePolicy {

	@Override
	public void onContentUpdate(final NodeRef bundleNodeRef, final boolean newContent) {
		if (newContent == false) {
			getBundleService().updateBundle(bundleNodeRef);
		}
		// Note: new content will be handled by BundleFolderBehaviour
	}

	@Override
	public void beforeDeleteNode(final NodeRef bundleNodeRef) {
		getBundleService().uninstallBundle(bundleNodeRef);
	}

	@Override
	public void register() {
		getPolicyComponent().bindClassBehaviour(ContentServicePolicies.OnContentUpdatePolicy.QNAME,
				BundleModel.TYPE_MANAGED_BUNDLE,
				new JavaBehaviour(this, "onContentUpdate", NotificationFrequency.TRANSACTION_COMMIT));
		getPolicyComponent().bindClassBehaviour(NodeServicePolicies.BeforeDeleteNodePolicy.QNAME,
				BundleModel.TYPE_MANAGED_BUNDLE,
				new JavaBehaviour(this, "beforeDeleteNode", NotificationFrequency.TRANSACTION_COMMIT));
	}

}
