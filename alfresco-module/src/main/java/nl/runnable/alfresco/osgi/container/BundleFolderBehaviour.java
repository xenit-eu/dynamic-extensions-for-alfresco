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

import nl.runnable.alfresco.osgi.BundleManifestInfo;
import nl.runnable.alfresco.osgi.BundleModel;
import nl.runnable.alfresco.repository.node.NodeHelper;
import nl.runnable.alfresco.repository.query.QueryBuilderFactory;
import nl.runnable.alfresco.repository.query.QueryHelper;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

/**
 * Handles the installation and uninstallation of Bundles stored in folders of type
 * {@link BundleModel#TYPE_BUNDLE_FOLDER}.
 * 
 * @author Laurens Fridael
 * @deprecated Repository bundle management will be removed in the future.
 */
@Deprecated
public class BundleFolderBehaviour extends AbstractBundleBehaviour implements
		NodeServicePolicies.OnCreateChildAssociationPolicy, NodeServicePolicies.BeforeDeleteChildAssociationPolicy {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	/* Dependencies */

	private NodeService nodeService;

	private NodeHelper nodeHelper;

	private ContentService contentService;

	private BundleHelper bundleHelper;

	private QueryBuilderFactory queryBuilderFactory;

	private QueryHelper queryHelper;

	private RepositoryHelper repositoryHelper;

	/* Main operations */

	@Override
	public void onCreateChildAssociation(final ChildAssociationRef childAssociationRef, final boolean newNode) {
		if (newNode) {
			return;
		}
		if (getNodeHelper().isOfType(childAssociationRef.getChildRef(), ContentModel.TYPE_CONTENT)) {
			final NodeRef nodeRef = childAssociationRef.getChildRef();
			if (logger.isDebugEnabled()) {
				logger.debug("Node {} added to BundleFolder.", nodeRef);
			}
			if (getNodeService().hasAspect(nodeRef, ContentModel.ASPECT_WORKING_COPY)) {
				if (logger.isDebugEnabled()) {
					logger.debug("Node {} is a working copy.", nodeRef);
				}
				// Ignore working copies.
				return;
			}
			if (getRepositoryHelper().isJavaArchive(nodeRef)) {
				if (logger.isDebugEnabled()) {
					logger.debug("Node {} is not a Java Archive.", nodeRef);
				}
				return;
			}
			final BundleManifestInfo bundleManifestInfo = getBundleService().getBundleManifestInfo(nodeRef);
			if (bundleManifestInfo == null) {
				if (logger.isDebugEnabled()) {
					logger.debug("Could not obtain OSGI Manifest information for node {}.", nodeRef);
				}
				return;
			}
			final String symbolicName = bundleManifestInfo.getSymbolicName();
			final String version = bundleManifestInfo.getVersion();
			/*
			 * TODO: Handle the IllegalArgumentException that is thrown in case of the version number not being
			 * OSGI-compliant.
			 */
			if (getBundleService().isBundleInstalled(symbolicName, version) == false) {
				final ManagedBundle bundleInfo = getBundleService().installBundle(nodeRef);
				if (bundleInfo != null && getBundleService().startBundleAutomatically(bundleInfo.getNodeRef())) {
					getBundleHelper().startBundle(bundleInfo.getBundle());
				}
			} else {
				/* TODO: Handle duplicate bundles. */
			}
		}
	}

	@Override
	public void beforeDeleteChildAssociation(final ChildAssociationRef childAssociationRef) {
		final NodeRef bundleNodeRef = childAssociationRef.getChildRef();
		if (getNodeHelper().isOfType(bundleNodeRef, BundleModel.TYPE_MANAGED_BUNDLE)) {
			getBundleService().uninstallBundle(bundleNodeRef);
		}
	}

	@Override
	public void register() {
		getPolicyComponent().bindAssociationBehaviour(NodeServicePolicies.OnCreateChildAssociationPolicy.QNAME,
				BundleModel.TYPE_BUNDLE_FOLDER,
				new JavaBehaviour(this, "onCreateChildAssociation", NotificationFrequency.TRANSACTION_COMMIT));
		getPolicyComponent().bindAssociationBehaviour(NodeServicePolicies.BeforeDeleteChildAssociationPolicy.QNAME,
				BundleModel.TYPE_BUNDLE_FOLDER,
				new JavaBehaviour(this, "beforeDeleteChildAssociation", NotificationFrequency.TRANSACTION_COMMIT));
	}

	/* Dependencies */

	@Required
	public void setNodeService(final NodeService nodeService) {
		Assert.notNull(nodeService);
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

	@Required
	public void setContentService(final ContentService contentService) {
		Assert.notNull(contentService);
		this.contentService = contentService;
	}

	protected ContentService getContentService() {
		return contentService;
	}

	@Required
	public void setBundleHelper(final BundleHelper bundleHelper) {
		Assert.notNull(bundleHelper);
		this.bundleHelper = bundleHelper;
	}

	protected BundleHelper getBundleHelper() {
		return bundleHelper;
	}

	@Required
	public void setQueryBuilderFactory(final QueryBuilderFactory queryBuilderFactory) {
		Assert.notNull(queryBuilderFactory);
		this.queryBuilderFactory = queryBuilderFactory;
	}

	protected QueryBuilderFactory getQueryBuilderFactory() {
		return queryBuilderFactory;
	}

	@Required
	public void setQueryHelper(final QueryHelper queryHelper) {
		Assert.notNull(queryHelper);
		this.queryHelper = queryHelper;
	}

	protected QueryHelper getQueryHelper() {
		return queryHelper;
	}

	@Required
	public void setRepositoryHelper(final RepositoryHelper repositoryHelper) {
		this.repositoryHelper = repositoryHelper;
	}

	protected RepositoryHelper getRepositoryHelper() {
		return repositoryHelper;
	}

}
