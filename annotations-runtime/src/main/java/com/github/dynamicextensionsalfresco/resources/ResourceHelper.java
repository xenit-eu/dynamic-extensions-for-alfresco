package com.github.dynamicextensionsalfresco.resources;

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
 * Shared resource functionality for services syncing resources with the repository.
 *
 * @author Laurent Van der Linden
 */
public class ResourceHelper {
    private final static Logger logger = LoggerFactory.getLogger(ResourceHelper.class);

    @Autowired
    protected NodeService nodeService;

    @Autowired
    protected ContentService contentService;

    @Autowired
    protected NamespaceService namespaceService;

    @Autowired
    protected SearchService searchService;

    public boolean nodeDiffersFromResource(Resource resource, RepositoryLocation targetLocation) throws IOException {
        final NodeRef nodeRef = findNodeForResource(resource, targetLocation);
        return nodeRef == null || nodeDiffersFromResource(resource, nodeRef);
    }

    public boolean nodeDiffersFromResource(Resource resource, NodeRef nodeRef) throws IOException {
        final ContentReader reader = contentService.getReader(nodeRef, ContentModel.PROP_CONTENT);
        if (IOUtils.contentEquals(reader.getContentInputStream(), resource.getInputStream())) {
            logger.debug("Existing repository node ({}) matches resource {}.", nodeRef, resource);
            return false;
        }
        return true;
    }

    public NodeRef findNodeForResource(Resource resource, RepositoryLocation repoLocation) {
        NodeRef rootNode = nodeService.getRootNode(repoLocation.getStoreRef());
        final List<NodeRef> nodeRefs = searchService.selectNodes(rootNode,
            String.format("%s/cm:%s",
                repoLocation.getPath(), ISO9075.encode(resource.getFilename())
            ), null, namespaceService, false);
        if (nodeRefs.isEmpty()) {
            return null;
        } else if (nodeRefs.size() == 1) {
            return nodeRefs.get(0);
        } else {
            throw new IllegalStateException(String.format("Found more than 1 node for resource %s: %s.", resource, nodeRefs));
        }
    }
}
