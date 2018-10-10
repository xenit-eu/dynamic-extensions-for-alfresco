package com.github.dynamicextensionsalfresco.resources;

import org.alfresco.service.cmr.repository.NodeRef;
import org.springframework.core.io.Resource;

/**
 * @author Laurent Van der Linden
 */
public class StaticUpdateStrategy implements UpdateStrategy {
    private final boolean strategy;

    public StaticUpdateStrategy(boolean strategy) {
        this.strategy = strategy;
    }

    @Override
    public boolean updateNode(Resource resource, NodeRef nodeRef) {
        return strategy;
    }
}
