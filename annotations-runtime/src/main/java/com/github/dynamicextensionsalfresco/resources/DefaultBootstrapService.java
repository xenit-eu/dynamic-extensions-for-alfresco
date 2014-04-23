package com.github.dynamicextensionsalfresco.resources;

import com.github.dynamicextensionsalfresco.annotations.AlfrescoService;
import com.github.dynamicextensionsalfresco.annotations.ServiceType;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.dictionary.RepositoryLocation;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.Assert;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Laurent Van der Linden
 */
public class DefaultBootstrapService implements ResourceLoaderAware, BootstrapService {
    private final static Logger logger = LoggerFactory.getLogger(DefaultBootstrapService.class);

    @Autowired
    protected ResourceHelper resourceHelper;

    @Autowired @AlfrescoService(ServiceType.LOW_LEVEL)
    protected ContentService contentService;

    @Autowired @AlfrescoService(ServiceType.LOW_LEVEL)
    protected NodeService nodeService;

    @Autowired @AlfrescoService(ServiceType.LOW_LEVEL)
    protected SearchService searchService;

    @Autowired @AlfrescoService(ServiceType.LOW_LEVEL)
    protected NamespaceService namespaceService;

    @Autowired
    protected FileFolderService fileFolderService;

    @Autowired
    protected MimetypeService mimetypeService;

    private ResourcePatternResolver resourcePatternResolver;

    @Override
    public Map<Resource,NodeRef> deployResources(String resourcePattern, RepositoryLocation targetLocation,
                                                 UpdateStrategy updateStrategy, String encoding, String mimetype,
                                                 QName nodeType) throws IOException {
        final Map<Resource,NodeRef> nodeReferences = new HashMap<Resource, NodeRef>();
        final Resource[] resources = resourcePatternResolver.getResources(resourcePattern);
        for (Resource resource : resources) {
            final NodeRef result = deployResource(targetLocation, updateStrategy, encoding, mimetype, resource, nodeType);
            nodeReferences.put(resource, result);
        }
        return nodeReferences;
    }

    @Override
    public NodeRef deployResource(RepositoryLocation targetLocation, UpdateStrategy updateStrategy, String encoding,
                                  String mimetype, Resource resource, QName nodeType) throws IOException {
        final NodeRef existingNode = resourceHelper.findNodeForResource(resource, targetLocation);
        if (existingNode == null) {
            final NodeRef nodeRef = createNode(resource, targetLocation, encoding, mimetype, nodeType);
            logger.debug("Deployed {} as new node {}.", resource, nodeRef);
            return nodeRef;
        } else {
            if (updateStrategy.updateNode(resource, existingNode)) {
                updateNode(resource, existingNode);
                logger.debug("Updated {} to existing node {}.", resource, existingNode);
            } else {
                logger.debug("No changes detected between resource {} and node {}.", resource, existingNode);
            }
            return existingNode;
        }
    }

    @Override
    public void updateNode(Resource resource, NodeRef existingNode) throws IOException {
        contentService.getWriter(existingNode, ContentModel.PROP_CONTENT, true).putContent(resource.getInputStream());
    }

    @Override
    public NodeRef createNode(Resource resource, RepositoryLocation targetLocation, String encoding, String mimetype,
                              QName nodeType) throws IOException {
        NodeRef rootNode = nodeService.getRootNode(targetLocation.getStoreRef());
        final List<NodeRef> parentNodes = searchService.selectNodes(rootNode, targetLocation.getPath(), null, namespaceService, false);
        Assert.isTrue(parentNodes.size() == 1, "Target location leads to not 1 unique Node reference");

        final String fileName = resource.getFilename();
        final FileInfo fileInfo = fileFolderService.create(parentNodes.get(0), fileName, nodeType);
        final NodeRef nodeRef = fileInfo.getNodeRef();

        final ContentWriter writer = contentService.getWriter(nodeRef, ContentModel.PROP_CONTENT, true);
        writer.putContent(resource.getInputStream());

        if (mimetype == null) {
            mimetype = guessMimetype(resource);
        }

        if (encoding == null) {
            encoding = guessEncoding(resource.getInputStream(), mimetype);
        }

        writer.setMimetype(mimetype);
        writer.setEncoding(encoding);

        return nodeRef;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourcePatternResolver = (ResourcePatternResolver) resourceLoader;
    }

    @Override
    public String guessMimetype(Resource resource) {
        return mimetypeService.guessMimetype(resource.getFilename());
    }

    @Override
    public String guessEncoding(InputStream in, String mimetype) {
        String encoding = "UTF-8";
        try {
            if (in != null) {
                // The InputStream must support marks
                final BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
                Charset charset = mimetypeService.getContentCharsetFinder().getCharset(bufferedInputStream, mimetype);
                encoding = charset.name();
            }
        } finally {
            IOUtils.closeQuietly(in);
        }
        return encoding;
    }
}
