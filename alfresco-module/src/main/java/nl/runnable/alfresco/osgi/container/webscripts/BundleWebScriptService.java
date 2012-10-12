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

package nl.runnable.alfresco.osgi.container.webscripts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarInputStream;

import nl.runnable.alfresco.osgi.BundleManifestInfo;
import nl.runnable.alfresco.osgi.BundleManifestInfoService;
import nl.runnable.alfresco.osgi.MissingBundleManifestAttributeException;
import nl.runnable.alfresco.osgi.container.BundleHelper;
import nl.runnable.alfresco.osgi.container.BundleService;
import nl.runnable.alfresco.osgi.container.BundleType;
import nl.runnable.alfresco.osgi.container.ManagedBundle;
import nl.runnable.alfresco.repository.node.NodeHelper;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.launch.Framework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.webscripts.WebScript;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Handles {@link Bundle}-related operatons from {@link WebScript}s.
 * 
 * @author Laurens Fridael
 * 
 */
public class BundleWebScriptService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	/* Dependencies */

	private final BundleManifestInfoService bundleManifestInfoService = new BundleManifestInfoService();

	private BundleService bundleService;

	private FileFolderService fileFolderService;

	private NodeService nodeService;

	private NodeHelper nodeHelper;

	private Framework framework;

	private final BundleHelper bundleHelper = new BundleHelper();

	/**
	 * Installs a new, or updates an existing, {@link Bundle} from the given {@link Content} under a given filename.
	 * This implementation will generate the filename if none is provided. It will also change the filename of an
	 * existing Bundle if it does not match.
	 * 
	 * @param content
	 * @param filename
	 *            The filename.
	 * @throws IOException
	 * @see {@link #generateFilename(BundleManifestInfo)}
	 */
	public BundleInfo installOrUpdateBundle(final Content content, String filename) throws IOException {
		Assert.notNull(content, "Content cannot be null.");

		BundleInfo bundleInfo = null;
		final TemporaryContent temporaryContent = new TemporaryContent(content);
		try {
			final JarInputStream jar = new JarInputStream(temporaryContent.getInputStream());
			try {
				final BundleService bundleService = getBundleService();
				final BundleManifestInfo bundleManifestInfo = getBundleManifestInfoService().parseBundleManifestInfo(
						jar);
				ManagedBundle managedBundle = bundleService.getManagedBundle(bundleManifestInfo.getSymbolicName(),
						bundleManifestInfo.getVersion());
				if (StringUtils.hasText(filename) == false) {
					filename = generateFilename(bundleManifestInfo);
				}
				if (managedBundle != null) {
					// Update existing Bundle.
					final NodeRef nodeRef = managedBundle.getNodeRef();
					updateBundleContent(nodeRef, temporaryContent);
					bundleService.updateBundle(nodeRef);
					// Change the filename if necessary.
					final NodeService nodeService = getNodeService();
					final String name = (String) nodeService.getProperty(nodeRef, ContentModel.PROP_NAME);
					if (name.equals(filename) == false) {
						nodeService.setProperty(nodeRef, ContentModel.PROP_NAME, filename);
					}
				} else {
					// Install new Bundle.
					final NodeRef nodeRef = storeBundleContent(temporaryContent, filename);
					managedBundle = bundleService.installBundle(nodeRef);
				}
				if (managedBundle != null) {
					bundleInfo = new BundleInfo(managedBundle.getBundle());
				}
			} catch (final MissingBundleManifestAttributeException e) {
				logger.warn("Missing Bundle attribute in manifest: {} ", e.getMissingAttribute());
			}
		} finally {
			temporaryContent.deleteTempFile();
		}

		return bundleInfo;
	}

	/**
	 * Generates a filename using the given {@link BundleManifestInfo}. This implementation uses the symbolic name and
	 * version. This will be called when no filename is provided.
	 * 
	 * @param bundleManifestInfo
	 * @return
	 */
	protected String generateFilename(final BundleManifestInfo bundleManifestInfo) {
		return String.format("%s-%s.jar", bundleManifestInfo.getSymbolicName(), bundleManifestInfo.getVersion());
	}

	protected NodeRef storeBundleContent(final Content content, final String filename) {
		Assert.notNull(content, "Content cannot be null.");
		Assert.hasText(filename, "Filename cannot be empty.");

		final NodeRef bundleNodeRef = null;
		final NodeRef bundleFolderNodeRef = getBundleService().getBundleFolderFor(BundleType.REPOSITORY_EXTENSIONS);
		if (bundleFolderNodeRef != null) {
			final FileInfo fileInfo = getFileFolderService().create(bundleFolderNodeRef, filename,
					ContentModel.TYPE_CONTENT);
			if (fileInfo != null) {
				final NodeRef nodeRef = fileInfo.getNodeRef();
				writeContent(nodeRef, content);
			}
		}
		return bundleNodeRef;
	}

	protected void updateBundleContent(final NodeRef nodeRef, final Content content) {
		Assert.notNull(content, "Content cannot be null.");
		writeContent(nodeRef, content);
	}

	protected void writeContent(final NodeRef nodeRef, final Content content) {
		Assert.notNull(nodeRef, "NodeRef cannot be null.");
		Assert.notNull(content, "Content cannot be null.");

		final ContentWriter writer = getFileFolderService().getWriter(nodeRef);
		writer.setMimetype(content.getMimetype());
		writer.setEncoding(content.getEncoding());
		writer.putContent(content.getInputStream());
	}

	/**
	 * Obtains information on the {@link Framework} as a {@link BundleInfo} instance.
	 * 
	 * @return
	 */
	public BundleInfo getFrameworkBundle() {
		return new BundleInfo(getFramework());
	}

	/**
	 * Obtains information on all {@link ManagedBundle}s as {@link BundleInfo} instances.
	 * 
	 * @return
	 */
	public List<BundleInfo> getManagedBundles() {
		final List<BundleInfo> bundles = new ArrayList<BundleInfo>();
		for (final ManagedBundle managedBundle : getBundleService().getManagedBundles()) {
			bundles.add(new BundleInfo(managedBundle.getBundle()));
		}
		return bundles;
	}

	public BundleInfo getManagedBundle(final long bundleId) {
		BundleInfo bundleInfo = null;
		for (final ManagedBundle managedBundle : getBundleService().getManagedBundles()) {
			final Bundle bundle = managedBundle.getBundle();
			if (bundle.getBundleId() == bundleId) {
				bundleInfo = new BundleInfo(bundle);
				break;
			}
		}
		return bundleInfo;
	}

	public void startFramework() {
		try {
			getFramework().start();
		} catch (final BundleException e) {
			logger.warn("Error starting Framework", e);
		}
	}

	public void stopFramework() {
		try {
			getFramework().stop();
		} catch (final BundleException e) {
			logger.warn("Error stopping Framework", e);
		}
	}

	public void startBundle(final long bundleId) {
		final Bundle bundle = getFramework().getBundleContext().getBundle(bundleId);
		if (bundle != null) {
			getBundleHelper().startBundle(bundle);
		}
	}

	public void stopBundle(final long bundleId) {
		final Bundle bundle = getFramework().getBundleContext().getBundle(bundleId);
		if (bundle != null) {
			getBundleHelper().stopBundle(bundle);
		}
	}

	/* Dependencies */

	@Required
	public void setBundleService(final BundleService bundleService) {
		Assert.notNull(bundleService);
		this.bundleService = bundleService;
	}

	protected BundleService getBundleService() {
		return bundleService;
	}

	protected BundleManifestInfoService getBundleManifestInfoService() {
		return bundleManifestInfoService;
	}

	@Required
	public void setFileFolderService(final FileFolderService fileFolderService) {
		Assert.notNull(fileFolderService);
		this.fileFolderService = fileFolderService;
	}

	protected FileFolderService getFileFolderService() {
		return fileFolderService;
	}

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
	public void setFramework(final Framework framework) {
		this.framework = framework;
	}

	protected Framework getFramework() {
		return framework;
	}

	protected BundleHelper getBundleHelper() {
		return bundleHelper;
	}

}
