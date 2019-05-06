package com.github.dynamicextensionsalfresco.resources;

import org.alfresco.service.cmr.repository.NodeRef;
import org.springframework.core.io.Resource;

import java.io.IOException;

/**
 * @author Laurent Van der Linden
 */
public class ContentCompareStrategy implements UpdateStrategy {
    private final ResourceHelper resourceHelper;

    public ContentCompareStrategy(ResourceHelper resourceHelper) {
        this.resourceHelper = resourceHelper;
    }

    @Override
    public boolean updateNode(Resource resource, NodeRef nodeRef) {
        try {
            return resourceHelper.nodeDiffersFromResource(resource, nodeRef);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
