package com.github.dynamicextensionsalfresco.osgi;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides operations for storing OSGi bundles and framework configuration in the repository.
 * 
 * @author Laurens Fridael
 * 
 */
public class RepositoryStoreService {

	private static final String DEFAULT_BUNDLE_REPOSITORY_LOCATION = "/Company Home/Data Dictionary/Dynamic Extensions/Bundles";

	/* Dependencies */

	private NodeService nodeService;

	private NamespacePrefixResolver namespacePrefixResolver;

	private FileFolderService fileFolderService;

    private PermissionService permissionService;

	/* Configuration */

	private String baseFolderDescription;

	private String bundleFolderDescription;

	private String configurationFolderDescription;

	private String bundleRepositoryLocation = DEFAULT_BUNDLE_REPOSITORY_LOCATION;

	/* Main operations */

	public NodeRef getBaseFolder(final boolean createIfNotExists) {
		final QName name = qName("cm", "dynamic_extensions");
		final NodeRef dataDictionary = getDataDictionary();
		NodeRef nodeRef = getChildOf(dataDictionary, name);
		if (nodeRef == null && createIfNotExists) {
			nodeRef = createFolder(dataDictionary, name, "Dynamic Extensions", getBaseFolderDescription());
            permissionService.setInheritParentPermissions(nodeRef, false);
		}
		return nodeRef;
	}

	public NodeRef getBundleFolder(final boolean createIfNotExists) {
		final QName name = qName("cm", "bundles");
		final NodeRef baseFolder = getBaseFolder(createIfNotExists);
		NodeRef nodeRef = getChildOf(baseFolder, name);
		if (nodeRef == null && createIfNotExists) {
			nodeRef = createFolder(baseFolder, name, "Bundles", getBundleFolderDescription());
		}
		return nodeRef;
	}

	public NodeRef getConfigurationFolder(final boolean createIfNotExists) {
		final QName name = qName("cm", "configuration");
		final NodeRef baseFolder = getBaseFolder(createIfNotExists);
		NodeRef nodeRef = getChildOf(baseFolder, name);
		if (nodeRef == null && createIfNotExists) {
			nodeRef = createFolder(baseFolder, name, "Configuration", getConfigurationFolderDescription());
		}
		return nodeRef;
	}

	/**
	 * Obtains information on the JAR files in the bundle folder.
	 * 
	 * @return
	 */
	public List<FileInfo> getBundleJarFiles() {
		final List<FileInfo> jarFiles = new ArrayList<FileInfo>();
		final NodeRef bundleFolder = getBundleFolder(false);
		if (bundleFolder != null) {
			for (final FileInfo file : getFileFolderService().listFiles(bundleFolder)) {
				if (file.getName().endsWith(".jar")) {
					jarFiles.add(file);
				}
			}
		}
		return jarFiles;
	}

	/* Utility operations */

	protected NodeRef getDataDictionary() {
		return getChildOf(getCompanyHome(), qName("app", "dictionary"));
	}

	protected NodeRef getCompanyHome() {
		return getChildOfRootNode(qName("app", "company_home"));
	}

	protected NodeRef createFolder(final NodeRef parentFolder, final QName qName, final String name,
			final String description) {
		return AuthenticationUtil.runAs(new RunAsWork<NodeRef>() {

			@Override
			public NodeRef doWork() throws Exception {
				final ChildAssociationRef childAssoc = nodeService.createNode(parentFolder,
						ContentModel.ASSOC_CONTAINS, qName, ContentModel.TYPE_FOLDER);
				if (childAssoc != null) {
					final NodeRef nodeRef = childAssoc.getChildRef();
					getNodeService().setProperty(nodeRef, ContentModel.PROP_NAME, name);
					if (StringUtils.hasText(description)) {
						getNodeService().setProperty(nodeRef, ContentModel.PROP_DESCRIPTION, description.trim());
					}
					return nodeRef;
				} else {
					return null;
				}
			}
		}, AuthenticationUtil.getSystemUserName());
	}

	/**
	 * Obtains the child with the given association type, {@link QName} of the given node.
	 * 
	 * @param nodeRef
	 * @param assocName
	 * @return the child with the given name or null if nodeRef is null
	 */
	protected NodeRef getChildOf(final NodeRef nodeRef, final QName assocType, final QName assocName) {
		if (nodeRef == null) {
			return null;
		}
		final List<ChildAssociationRef> childAssocs = getNodeService().getChildAssocs(nodeRef, assocType, assocName);
		if (childAssocs.isEmpty() == false) {
			return childAssocs.get(0).getChildRef();
		} else {
			return null;
		}
	}

	/**
	 * Obtains the child with the given {@link QName} for the association type {@link ContentModel#ASSOC_CONTAINS}.
	 * 
	 * @param nodeRef
	 * @param qName
	 * @return the child with the given name or null if nodeRef is null
	 */
	protected NodeRef getChildOf(final NodeRef nodeRef, final QName qName) {
		return getChildOf(nodeRef, ContentModel.ASSOC_CONTAINS, qName);
	}

	/**
	 * Obtains the child with the given name from the {@link StoreRef#STORE_REF_WORKSPACE_SPACESSTORE} root node.
	 * 
	 * @param qName
	 * @return
	 */
	protected NodeRef getChildOfRootNode(final QName qName) {
		return getChildOf(getNodeService().getRootNode(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE),
				ContentModel.ASSOC_CHILDREN, qName);
	}

	protected QName qName(final String prefix, final String localName) {
		return QName.createQName(prefix, localName, getNamespacePrefixResolver());
	}

	/* Dependencies */

	public void setNodeService(final NodeService nodeService) {
		Assert.notNull(nodeService);
		this.nodeService = nodeService;
	}

	protected NodeService getNodeService() {
		return nodeService;
	}

	public void setNamespacePrefixResolver(final NamespacePrefixResolver namespacePrefixResolver) {
		Assert.notNull(namespacePrefixResolver);
		this.namespacePrefixResolver = namespacePrefixResolver;
	}

	protected NamespacePrefixResolver getNamespacePrefixResolver() {
		return namespacePrefixResolver;
	}

	public void setFileFolderService(final FileFolderService fileFolderService) {
		Assert.notNull(fileFolderService);
		this.fileFolderService = fileFolderService;
	}

	protected FileFolderService getFileFolderService() {
		return fileFolderService;
	}

	/* Configuration */

	protected String getBaseFolderDescription() {
		return baseFolderDescription;
	}

	public void setBaseFolderDescription(final String baseFolderDescription) {
		this.baseFolderDescription = baseFolderDescription;
	}

	protected String getBundleFolderDescription() {
		return bundleFolderDescription;
	}

	public void setBundleFolderDescription(final String bundleFolderDescription) {
		this.bundleFolderDescription = bundleFolderDescription;
	}

	protected String getConfigurationFolderDescription() {
		return configurationFolderDescription;
	}

	public void setConfigurationFolderDescription(final String configurationFolderDescription) {
		this.configurationFolderDescription = configurationFolderDescription;
	}

    public void setPermissionService(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    public void setBundleRepositoryLocation(final String bundleRepositoryLocation) {
		Assert.hasText(bundleRepositoryLocation);
		this.bundleRepositoryLocation = bundleRepositoryLocation;
	}

	public String getBundleRepositoryLocation() {
		return bundleRepositoryLocation;
	}

}
