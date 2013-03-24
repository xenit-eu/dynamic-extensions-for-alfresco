package nl.runnable.alfresco.extensions.impl;

import java.io.InputStream;
import java.util.List;

import nl.runnable.alfresco.extensions.RepositoryFolderService;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;
import org.springframework.util.StringUtils;

public class RepositoryFolderServiceImpl implements RepositoryFolderService {

	/* Dependencies */

	private NodeService nodeService;

	private NamespacePrefixResolver namespacePrefixResolver;

	/* Configuration */

	private Descriptions descriptions = new Descriptions();

	/* Main operations */

	@Override
	public NodeRef getBaseFolder() {
		final QName name = qName("cm", "dynamic_extensions");
		final NodeRef dataDictionary = getDataDictionary();
		NodeRef nodeRef = getChildOf(dataDictionary, name);
		if (nodeRef == null) {
			nodeRef = createFolder(dataDictionary, name, "Dynamic Extensions", getDescriptions().getBaseFolder());
		}
		return nodeRef;
	}

	@Override
	public NodeRef getBundleFolder() {
		final QName name = qName("cm", "bundles");
		final NodeRef baseFolder = getBaseFolder();
		NodeRef nodeRef = getChildOf(baseFolder, name);
		if (nodeRef == null) {
			nodeRef = createFolder(baseFolder, name, "Bundles", getDescriptions().getBundleFolder());
		}
		return nodeRef;
	}

	@Override
	public NodeRef getConfigurationFolder() {
		final QName name = qName("cm", "configuration");
		final NodeRef baseFolder = getBaseFolder();
		NodeRef nodeRef = getChildOf(baseFolder, name);
		if (nodeRef == null) {
			nodeRef = createFolder(baseFolder, name, "Configuration", getDescriptions().getConfigurationFolder());
		}
		return nodeRef;
	}

	@Override
	public NodeRef getInstallationHistoryFolder() {
		final QName name = qName("cm", "installation_history");
		final NodeRef baseFolder = getBaseFolder();
		NodeRef nodeRef = getChildOf(baseFolder, name);
		if (nodeRef == null) {
			nodeRef = createFolder(baseFolder, name, "Installation History", getDescriptions()
					.getInstallationHistoryFolder());
		}
		return nodeRef;
	}

	@Override
	public void saveExtension(final InputStream data) {
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
		return AuthenticationUtil.runAsSystem(new RunAsWork<NodeRef>() {

			@Override
			public NodeRef doWork() throws Exception {
				final ChildAssociationRef childAssoc = nodeService.createNode(parentFolder,
						ContentModel.ASSOC_CONTAINS, qName, ContentModel.TYPE_FOLDER);
				final NodeRef nodeRef = childAssoc.getChildRef();
				getNodeService().setProperty(nodeRef, ContentModel.PROP_NAME, name);
				if (StringUtils.hasText(description)) {
					getNodeService().setProperty(nodeRef, ContentModel.PROP_DESCRIPTION, description.trim());
				}
				return nodeRef;
			}
		});
	}

	/**
	 * Obtains the child with the given association type, {@link QName} of the given node.
	 * 
	 * @param nodeRef
	 * @param assocName
	 * @return
	 */
	protected NodeRef getChildOf(final NodeRef nodeRef, final QName assocType, final QName assocName) {
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
	 * @return
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
		this.nodeService = nodeService;
	}

	protected NodeService getNodeService() {
		return nodeService;
	}

	public void setNamespacePrefixResolver(final NamespacePrefixResolver namespacePrefixResolver) {
		this.namespacePrefixResolver = namespacePrefixResolver;
	}

	protected NamespacePrefixResolver getNamespacePrefixResolver() {
		return namespacePrefixResolver;
	}

	/* Configuration */

	public void setDescriptions(final Descriptions descriptions) {
		this.descriptions = descriptions;
	}

	public Descriptions getDescriptions() {
		return descriptions;
	}
}
