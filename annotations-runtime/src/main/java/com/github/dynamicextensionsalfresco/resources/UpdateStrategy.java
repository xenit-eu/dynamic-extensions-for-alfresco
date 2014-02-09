package com.github.dynamicextensionsalfresco.resources;

import org.alfresco.service.cmr.repository.NodeRef;
import org.springframework.core.io.Resource;

import java.io.IOException;

/**
 * @author Laurent Van der Linden
 */
public interface UpdateStrategy {
    boolean updateNode(Resource resource, NodeRef nodeRef) throws IOException;
}
