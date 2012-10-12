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

import java.util.Collections;
import java.util.List;

import nl.runnable.alfresco.osgi.FrameworkManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

/**
 * @deprecated Repository bundle management will be removed in the future.
 * @author Laurens Fridael
 * 
 */
@Deprecated
public class OsgiContainerFrameworkManager extends FrameworkManager {

	private static final String DYNAMIC_EXTENSIONS_REPOSITORY_PATH = "/Data Dictionary/Dynamic Extensions/Extension Bundles";

	private final Logger logger = LoggerFactory.getLogger(getClass());

	/* Dependencies */

	private OsgiContainerBundleFolderPatch folderPatch;

	private BundleService bundleService;

	/* Configuration */

	private boolean repositoryBundleManagementEnabled = false;

	/* State */

	private List<AbstractBundleBehaviour> bundleBehaviours = Collections.emptyList();

	/* Operations */

	@Override
	public void initialize() {
		super.initialize();
		if (isRepositoryBundleManagementEnabled()) {
			/* Note: initialization MUST occur in this particular order: */
			createBundleFolders();
			installExistingBundles();
			registerBehaviours();
			if (logger.isInfoEnabled()) {
				logger.info("Repository bundle management is enabled. "
						+ "You can manage Dynamic Extensions from the repository folder '{}'",
						DYNAMIC_EXTENSIONS_REPOSITORY_PATH);
			}
		}
	}

	protected void createBundleFolders() {
		getFolderPatch().createBundleFolders();
	}

	protected void installExistingBundles() {
		getBundleService().installExistingBundles();
	}

	protected void registerBehaviours() {
		for (final AbstractBundleBehaviour behaviour : getBundleBehaviours()) {
			behaviour.register();
		}
	}

	/* Dependencies */

	@Required
	public void setFolderPatch(final OsgiContainerBundleFolderPatch folderPatch) {
		this.folderPatch = folderPatch;
	}

	protected OsgiContainerBundleFolderPatch getFolderPatch() {
		return folderPatch;
	}

	@Required
	public void setBundleService(final BundleService bundleService) {
		Assert.notNull(bundleService);
		this.bundleService = bundleService;
	}

	protected BundleService getBundleService() {
		return bundleService;
	}

	/* Configuration */

	public void setRepositoryBundleManagementEnabled(final boolean repositoryBundleManagementEnabled) {
		this.repositoryBundleManagementEnabled = repositoryBundleManagementEnabled;
	}

	public boolean isRepositoryBundleManagementEnabled() {
		return repositoryBundleManagementEnabled;
	}

	/* State */

	public void setBundleBehaviours(final List<AbstractBundleBehaviour> bundleBehaviours) {
		Assert.notNull(bundleBehaviours);
		this.bundleBehaviours = bundleBehaviours;
	}

	protected List<AbstractBundleBehaviour> getBundleBehaviours() {
		return bundleBehaviours;
	}

}
