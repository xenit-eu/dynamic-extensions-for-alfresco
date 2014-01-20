package com.github.dynamicextensionsalfresco.workflow;

import com.github.dynamicextensionsalfresco.ContentComparator;
import com.google.common.collect.ImmutableMap;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.dictionary.RepositoryLocation;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.workflow.WorkflowModel;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import static java.util.Arrays.asList;
import static org.alfresco.repo.security.authentication.AuthenticationUtil.runAsSystem;

/**
 * Service that inspect the {@link WorkflowDefinitionRegistrar#workflowLocationPattern} to find workflow definitions and
 * stores them in the Data Dictionary to enable update detection.
 * <br/>
 * Know issue: Alfresco has policies in place to deploy workflow definitions from  the Data Dictionary / Workflow Definitions
 * folder. However these show up as <i>(Nameless deployment)</i> in the Activiti admin console.
 *
 * @author Laurent Van der Linden
 */
public class WorkflowDefinitionRegistrar implements InitializingBean, ResourceLoaderAware {
    private static final String workflowLocationPattern = "osgibundle:/META-INF/alfresco/workflows/*.bpmn20.xml";
    private final static Logger logger = LoggerFactory.getLogger(WorkflowDefinitionRegistrar.class);

    @Autowired
    protected RepositoryLocation customWorkflowDefsRepositoryLocation;

    @Autowired
    protected ContentComparator contentComparator;

    @Autowired
    protected FileFolderService fileFolderService;

    @Autowired
    protected ContentService contentService;

    @Autowired
    protected NodeService nodeService;

    @Autowired
    protected SearchService searchService;

    @Autowired
    protected NamespaceService namespaceService;

    @Autowired
    protected TransactionService transactionService;

    private ResourcePatternResolver resourcePatternResolver;

    @Override
    public void afterPropertiesSet() throws Exception {
        runAsSystem(new AuthenticationUtil.RunAsWork<Object>() {
            @Override
            public Object doWork() throws Exception {
                try {
                    transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Object>() {
                        @Override
                        public Object execute() throws Throwable {
                            final List<Resource> workflowDefinitions = findWorkflowDefinitions();
                            for (Resource workflowDefinition : workflowDefinitions) {
                                if (contentComparator.nodeDiffersFromResource(workflowDefinition, customWorkflowDefsRepositoryLocation)) {
                                    final List<NodeRef> workflowNodes = contentComparator.findNodesForResource(workflowDefinition, customWorkflowDefsRepositoryLocation);
                                    if (workflowNodes.isEmpty()) {
                                        createNewDefinitionNode(workflowDefinition);
                                    } else {
                                        updateDefinitionNode(workflowDefinition, workflowNodes);
                                    }
                                    logger.info("Deployed workflow definition {}.", workflowDefinition.getFilename());
                                } else {
                                    logger.debug("Workflow definition {} is already deployed and uptodate.", workflowDefinition.getFilename());
                                }
                            }
                            return null;
                        }
                    }, false, false);
                } catch (Exception e) {
                    logger.error("tx error", e);
                }
                return null;
            }
        });
    }

    private void updateDefinitionNode(Resource workflowDefinition, List<NodeRef> workflowNodes) throws IOException {
        contentService.getWriter(workflowNodes.get(0), ContentModel.PROP_CONTENT, true).putContent(workflowDefinition.getInputStream());
    }

    private void createNewDefinitionNode(Resource workflowDefinition) throws IOException {
        NodeRef rootNode = nodeService.getRootNode(customWorkflowDefsRepositoryLocation.getStoreRef());
        final List<NodeRef> parentNodes = searchService.selectNodes(rootNode, customWorkflowDefsRepositoryLocation.getPath(), null, namespaceService, false);
        Assert.isTrue(parentNodes.size() == 1, "Custom workflow definition location leads to not 1 unique Node reference");

        final String fileName = workflowDefinition.getFilename();
        final FileInfo fileInfo = fileFolderService.create(parentNodes.get(0), fileName, WorkflowModel.TYPE_WORKFLOW_DEF);
        final NodeRef nodeRef = fileInfo.getNodeRef();

        final ContentWriter writer = contentService.getWriter(nodeRef, ContentModel.PROP_CONTENT, true);
        writer.putContent(workflowDefinition.getInputStream());
        writer.setMimetype(MimetypeMap.MIMETYPE_XML);
        writer.setEncoding("UTF-8");

        // rely on standard deployment policy: will deploy as nameless workflow definition
        nodeService.setProperty(nodeRef, WorkflowModel.PROP_WORKFLOW_DEF_ENGINE_ID, "activiti");
        nodeService.setProperty(nodeRef, WorkflowModel.PROP_WORKFLOW_DEF_NAME, fileName);
        nodeService.setProperty(nodeRef, WorkflowModel.PROP_WORKFLOW_DEFINITION_NAME, fileName);
        nodeService.addAspect(nodeRef, ContentModel.ASPECT_TITLED, ImmutableMap.<QName, Serializable>builder()
            .put(ContentModel.PROP_TITLE, fileName)
            .build()
        );
        nodeService.setProperty(nodeRef, WorkflowModel.PROP_WORKFLOW_DEF_DEPLOYED, true);
    }

    private List<Resource> findWorkflowDefinitions() throws IOException {
        return asList(resourcePatternResolver.getResources(workflowLocationPattern));
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourcePatternResolver = (ResourcePatternResolver) resourceLoader;
    }
}
