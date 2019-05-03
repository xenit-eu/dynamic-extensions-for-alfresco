package com.github.dynamicextensionsalfresco.resources;

import org.alfresco.repo.dictionary.RepositoryLocation;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Service to support bootstrapping resources into Alfresco at startup.
 *
 * @author Laurent Van der Linden
 */
public interface BootstrapService {
    /**
     * deploy all resources matching the pattern
     * @param resourcePattern a Spring resource pattern: ie. osgibundle:/META-INF/alfresco/bootstrap/*.xml
     * @param targetLocation target folder
     * @param updateStrategy should existing nodes by updated
     * @param encoding set to null for auto-guess
     * @param mimetype set to null for auto-guess
     * @param nodeType type for newly creates nodes
     * @return mapping of resource to nodereference (create or update)
     * @throws IOException
     */
    Map<Resource,NodeRef> deployResources(String resourcePattern, RepositoryLocation targetLocation,
                                          UpdateStrategy updateStrategy, String encoding, String mimetype,
                                          QName nodeType) throws IOException;

    NodeRef deployResource(RepositoryLocation targetLocation, UpdateStrategy updateStrategy, String encoding, String mimetype,
                           Resource resource, QName nodeType) throws IOException;

    void updateNode(Resource resource, NodeRef existingNode) throws IOException;

    NodeRef createNode(Resource resource, RepositoryLocation targetLocation, String encoding, String mimetype,
                       QName nodeType) throws IOException;

    String guessMimetype(Resource resource);

    String guessEncoding(InputStream in, String mimetype);
}
