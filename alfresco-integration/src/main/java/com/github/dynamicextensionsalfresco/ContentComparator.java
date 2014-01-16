package com.github.dynamicextensionsalfresco;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.dictionary.RepositoryLocation;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.util.ISO9075;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;

/**
 * Shared change detection functionality for services using the Data Dictionary.
 *
 * @author Laurent Van der Linden
 */
public class ContentComparator {
    private final static Logger logger = LoggerFactory.getLogger(ContentComparator.class);

    @Autowired
    protected NodeService nodeService;

    @Autowired
    protected ContentService contentService;

    @Autowired
    protected NamespaceService namespaceService;

    @Autowired
    protected SearchService searchService;

    public boolean nodeDiffersFromResource(Resource modelResource, RepositoryLocation repoLocation) throws IOException {
        List<NodeRef> nodeRefs = findNodesForResource(modelResource, repoLocation);
        if (nodeRefs.isEmpty() == false) {
            final NodeRef modelRef = nodeRefs.get(0);
            final ContentReader reader = contentService.getReader(modelRef, ContentModel.PROP_CONTENT);
            if (IOUtils.contentEquals(reader.getContentInputStream(), modelResource.getInputStream())) {
                logger.debug("Existing repository node is up to date.");
                return false;
            }
        }
        return true;
    }

    public List<NodeRef> findNodesForResource(Resource modelResource, RepositoryLocation repoLocation) {
        NodeRef rootNode = nodeService.getRootNode(repoLocation.getStoreRef());
        return searchService.selectNodes(rootNode,
            String.format("%s/cm:%s",
                repoLocation.getPath(), ISO9075.encode(modelResource.getFilename())
            ), null, namespaceService, false);
    }
}
