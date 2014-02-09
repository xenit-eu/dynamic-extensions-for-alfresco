package com.github.dynamicextensionsalfresco.resources;

import org.alfresco.service.cmr.repository.NodeRef;
import org.springframework.core.io.Resource;

/**
 * @author Laurent Van der Linden
 */
public class NoUpdateStrategy implements UpdateStrategy {
    @Override
    public boolean updateNode(Resource resource, NodeRef nodeRef) {
        return false;
    }
}
