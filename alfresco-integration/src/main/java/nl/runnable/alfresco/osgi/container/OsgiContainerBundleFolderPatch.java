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

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import nl.runnable.alfresco.osgi.BundleModel;
import nl.runnable.alfresco.repository.node.NodeHelper;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.module.ModuleComponent;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.MimetypeService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Alfresco {@link ModuleComponent} that patches the Alfreso repository with the folder structure used by the OSGi
 * container. The actual folder structure is specified by Spring configuration.
 * 
 * @author Laurens Fridael
 * @deprecated Repository bundle management will be removed in the future.
 */
@Deprecated
@Service
public class OsgiContainerBundleFolderPatch implements ResourceLoaderAware {

	public static class BundleFolder {

		private String path;

		private String description;

		private String files;

		private AutoStart autoStart = AutoStart.getDefault();

		@Required
		public void setPath(final String path) {
			Assert.hasText(path);
			this.path = path;
		}

		public String getPath() {
			return path;
		}

		public void setDescription(final String description) {
			this.description = description;
		}

		public String getDescription() {
			return description;
		}

		public void setFiles(final String files) {
			this.files = files;
		}

		public String getFiles() {
			return files;
		}

		public AutoStart getAutoStart() {
			return autoStart;
		}

		public void setAutoStart(final AutoStart autoStart) {
			this.autoStart = autoStart;
		}

	}

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private SearchService searchService;

	private NodeService nodeService;

	private NamespaceService namespaceService;

	private FileFolderService fileFolderService;

	private MimetypeService mimetypeService;

	private NodeHelper nodeHelper;

	private ResourcePatternResolver resourcePatternResolver;

	private List<BundleFolder> bundleFolders = Collections.emptyList();

	private boolean enabled = true;

	@Required
	public void setSearchService(final SearchService searchService) {
		Assert.notNull(searchService);
		this.searchService = searchService;
	}

	protected SearchService getSearchService() {
		return searchService;
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
	public void setNamespaceService(final NamespaceService namespaceService) {
		Assert.notNull(namespaceService);
		this.namespaceService = namespaceService;
	}

	protected NamespaceService getNamespaceService() {
		return namespaceService;
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
	public void setMimetypeService(final MimetypeService mimetypeService) {
		Assert.notNull(mimetypeService);
		this.mimetypeService = mimetypeService;
	}

	protected MimetypeService getMimetypeService() {
		return mimetypeService;
	}

	@Required
	public void setNodeHelper(final NodeHelper nodeHelper) {
		Assert.notNull(nodeHelper);
		this.nodeHelper = nodeHelper;
	}

	protected NodeHelper getNodeHelper() {
		return nodeHelper;
	}

	@Override
	public void setResourceLoader(final ResourceLoader resourceLoader) {
		Assert.isInstanceOf(ResourcePatternResolver.class, resourceLoader);
		this.resourcePatternResolver = (ResourcePatternResolver) resourceLoader;
	}

	protected ResourcePatternResolver getResourcePatternResolver() {
		return resourcePatternResolver;
	}

	public void setBundleFolders(final List<BundleFolder> bundleFolders) {
		Assert.notNull(bundleFolders);
		this.bundleFolders = bundleFolders;
	}

	public List<BundleFolder> getBundleFolders() {
		return bundleFolders;
	}

	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void createBundleFolders() {
		if (isEnabled() == false) {
			return;
		}
		for (final BundleFolder folder : getBundleFolders()) {
			final NodeRef folderNodeRef = createBundleFolderInCompanyHome(folder.getPath());
			if (folderNodeRef == null) {
				logger.warn("Cannot create folder: {}", folder.getPath());
				continue;
			}
			if (getFileFolderService().listFiles(folderNodeRef).isEmpty() == false) {
				// Folder is not empty, skip this folder to avoid overwriting any user-created files.
				continue;
			}
			getNodeService().setProperty(folderNodeRef, BundleModel.PROP_AUTOSTART_BUNDLES,
					folder.getAutoStart().name());
			if (StringUtils.hasText(folder.getDescription())) {
				getNodeService().addAspect(folderNodeRef, ContentModel.ASPECT_TITLED,
						Collections.<QName, Serializable> emptyMap());
				getNodeService().setProperty(folderNodeRef, ContentModel.PROP_DESCRIPTION,
						folder.getDescription().trim());
			}
			if (StringUtils.hasText(folder.getFiles())) {
				try {
					final List<Resource> files = Arrays.asList(getResourcePatternResolver().getResources(
							folder.getFiles()));
					for (final Resource file : files) {
						try {
							final String filename = file.getFilename();
							final String extension = (filename.lastIndexOf(".") != -1 ? filename.substring(filename
									.lastIndexOf(".") + 1) : null);
							String mimetype = null;
							if (StringUtils.hasText(extension)) {
								mimetype = getMimetypeService().getMimetype(extension);
							}
							if (logger.isDebugEnabled()) {
								logger.debug("Creating file {}/{}", new Object[] { folder.getPath(), filename });
							}
							final QName contentType = (MimetypeMap.MIMETYPE_ZIP.equalsIgnoreCase(mimetype) ? BundleModel.TYPE_MANAGED_BUNDLE
									: ContentModel.TYPE_CONTENT);
							final FileInfo fileInfo = getFileFolderService().create(folderNodeRef, filename,
									contentType);
							final ContentWriter writer = getFileFolderService().getWriter(fileInfo.getNodeRef());
							if (StringUtils.hasText(mimetype)) {
								writer.setMimetype(mimetype);
							}
							writer.putContent(file.getInputStream());
						} catch (final ContentIOException e) {
							logger.warn("Error writing file {}: {}",
									new Object[] { file.getFilename(), e.getMessage() });
						}
					}
				} catch (final IOException e) {
					logger.warn("Error populating folder {}: {}", new Object[] { folder.getPath(), e.getMessage() });
				}
			}
		}

	}

	/**
	 * Creates a folder of type {@link BundleModel#TYPE_BUNDLE_FOLDER} for a given path relative to Company Home .
	 * 
	 * @param path
	 * @return The NodeRef if the folder was created or already exists, null if it could not be created or already
	 *         exists and is not of the correct type.
	 */
	protected NodeRef createBundleFolderInCompanyHome(final String path) {
		Assert.hasText(path, "Path cannot be empty.");
		final List<String> names = new ArrayList<String>(Arrays.asList(path.split("/")));
		NodeRef currentNode = getNodeHelper().getCompanyHome();
		if (currentNode == null) {
			logger.info("Cannot find Company Home node. This is normal when running within an integration test.");
			return null;
		}
		Assert.state(currentNode != null);
		while (names.isEmpty() == false) {
			final String name = names.remove(0);
			if (name == null || name.isEmpty()) {
				continue;
			}
			final NodeRef childNode = getFileFolderService().searchSimple(currentNode, name);
			final QName folderType = (names.isEmpty() ? BundleModel.TYPE_BUNDLE_FOLDER : ContentModel.TYPE_FOLDER);
			if (childNode == null) {
				currentNode = getFileFolderService().create(currentNode, name, folderType).getNodeRef();
			} else {
				if (getNodeHelper().isOfType(childNode, folderType) == false) {
					// Not of expected folder type.
					return null;
				}
				currentNode = childNode;
			}
		}
		return currentNode;
	}

}
