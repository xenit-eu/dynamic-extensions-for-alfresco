package com.github.dynamicextensionsalfresco.resources;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Date;

/**
 * Compare time stamps
 *
 * @author Laurent Van der Linden
 */
public class TimestampCompareStrategy implements UpdateStrategy {
    private final static Logger logger = LoggerFactory.getLogger(TimestampCompareStrategy.class);

    private final NodeService nodeService;

    public TimestampCompareStrategy(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @Override
    public boolean updateNode(Resource resource, NodeRef nodeRef) throws IOException {
        final Date modifiedDate = (Date) nodeService.getProperty(nodeRef, ContentModel.PROP_MODIFIED);
        if (logger.isDebugEnabled()) {
            logger.debug(
                "JAR resource {} was last modified {}, the repository version is at {}",
                resource.getFilename(), new Date(resource.lastModified()), modifiedDate
            );
        }
        if (modifiedDate == null) {
            return true;
        }
        return resource.lastModified() > modifiedDate.getTime();
    }
}
