package nl.runnable.alfresco.repository.query;

import org.alfresco.query.CannedQueryFactory;
import org.alfresco.service.cmr.repository.NodeRef;

public interface CannedQueryHelper {

	public CannedQueryFactory<NodeRef> getFileFolderCannedQueryFactory();

}
