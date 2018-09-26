package com.github.dynamicextensionsalfresco.resources;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.dictionary.RepositoryLocation;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.cmr.search.QueryParameterDefinition;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * @author Laurent Van der Linden
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "resources-test-context.xml")
public class BootstrapTest {
    @Autowired
    protected ResourceHelper resourceHelper;

    @Autowired
    protected DefaultBootstrapService bootstrapService;

    @Autowired
    protected SearchService searchService;

    @Autowired
    protected ContentService contentService;

    @Test
    public void testDeployment() throws Exception {
        final StoreRef store = StoreRef.STORE_REF_WORKSPACE_SPACESSTORE;
        final NodeRef testNodeRef = new NodeRef(store.getProtocol(), store.getIdentifier(), UUID.randomUUID().toString());
        when(
            searchService.selectNodes(any(NodeRef.class),
                anyString(),
                any(QueryParameterDefinition[].class),
                any(NamespacePrefixResolver.class),
                anyBoolean()
            )).thenReturn(asList(testNodeRef));

        final ContentReader reader = mock(ContentReader.class);
        when(contentService.getReader(any(NodeRef.class), any(QName.class))).thenReturn(reader);
        when(reader.getContentInputStream()).thenReturn(new ByteArrayInputStream("test".getBytes()));
        when(contentService.getWriter(testNodeRef, ContentModel.PROP_CONTENT, true)).thenReturn(mock(ContentWriter.class));

        final Map<Resource, NodeRef> refMap = deploy(store);

        assertEquals("Expecting 1 node reference", 1, refMap.size());
        verify(contentService, never()).getWriter(any(NodeRef.class), any(QName.class), anyBoolean());

        when(reader.getContentInputStream()).thenReturn(new ByteArrayInputStream("test2".getBytes()));
        deploy(store);
        verify(contentService).getWriter(testNodeRef, ContentModel.PROP_CONTENT, true);
    }

    private Map<Resource, NodeRef> deploy(StoreRef store) throws IOException {
        return bootstrapService.deployResources(
                "classpath:/com/github/dynamicextensionsalfresco/resources/sample.txt",
                new RepositoryLocation(store, "/app:company_home/app:dictionary", SearchService.LANGUAGE_XPATH),
                new ContentCompareStrategy(resourceHelper),
                null, null, ContentModel.TYPE_CONTENT
            );
    }
}
