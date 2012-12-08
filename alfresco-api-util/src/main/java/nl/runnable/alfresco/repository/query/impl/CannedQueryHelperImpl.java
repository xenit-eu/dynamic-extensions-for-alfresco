package nl.runnable.alfresco.repository.query.impl;

import nl.runnable.alfresco.repository.query.CannedQueryHelper;

import org.alfresco.query.CannedQueryFactory;
import org.alfresco.repo.model.filefolder.FileFolderServiceImpl;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.registry.NamedObjectRegistry;

/**
 * We implement
 * 
 * @author Laurens Fridael
 * 
 */
public class CannedQueryHelperImpl implements CannedQueryHelper {

	/**
	 * Taken from {@link FileFolderServiceImpl}.
	 */
	private static final String CANNED_QUERY_FILEFOLDER_LIST = "fileFolderGetChildrenCannedQueryFactory";

	/* Dependencies */

	private NamedObjectRegistry<CannedQueryFactory<NodeRef>> fileFolderCannedQueryFactoryRegistry;

	/* Main operations */

	@Override
	public CannedQueryFactory<NodeRef> getFileFolderCannedQueryFactory() {
		return getFileFolderCannedQueryFactoryRegistry().getNamedObject(CANNED_QUERY_FILEFOLDER_LIST);
	}

	/* Dependencies */

	public void setFileFolderCannedQueryFactoryRegistry(
			final NamedObjectRegistry<CannedQueryFactory<NodeRef>> fileFolderCannedQueryRegistry) {
		this.fileFolderCannedQueryFactoryRegistry = fileFolderCannedQueryRegistry;
	}

	protected NamedObjectRegistry<CannedQueryFactory<NodeRef>> getFileFolderCannedQueryFactoryRegistry() {
		return fileFolderCannedQueryFactoryRegistry;
	}

}
