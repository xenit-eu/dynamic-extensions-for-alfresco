package com.github.dynamicextensionsalfresco.workflow;

import com.github.dynamicextensionsalfresco.resources.BootstrapService;
import com.github.dynamicextensionsalfresco.resources.ContentCompareStrategy;
import com.github.dynamicextensionsalfresco.resources.ResourceHelper;
import com.google.common.collect.ImmutableMap;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.dictionary.RepositoryLocation;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.workflow.WorkflowModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import java.io.Serializable;
import java.util.Map;

import static org.alfresco.repo.security.authentication.AuthenticationUtil.runAs;

/**
 * Service that inspect the {@link WorkflowDefinitionRegistrar#workflowLocationPattern} to find workflow definitions and
 * stores them in the Data Dictionary to enable update detection.
 * <br>
 * Know issue: Alfresco has policies in place to deploy workflow definitions from  the Data Dictionary / Workflow Definitions
 * folder. However these show up as <i>(Nameless deployment)</i> in the Activiti admin console.
 *
 * @author Laurent Van der Linden
 */
public class WorkflowDefinitionRegistrar implements InitializingBean {
    private static final String workflowLocationPattern = "osgibundle:/META-INF/alfresco/workflows/*.bpmn20.xml";
    private final static Logger logger = LoggerFactory.getLogger(WorkflowDefinitionRegistrar.class);

    @Autowired
    protected RepositoryLocation customWorkflowDefsRepositoryLocation;

    @Autowired
    protected ResourceHelper resourceHelper;

    @Autowired
    protected BootstrapService bootstrapService;

    @Autowired
    protected NodeService nodeService;

    @Autowired
    protected TransactionService transactionService;

    @Override
    public void afterPropertiesSet() throws Exception {
        runAs(new AuthenticationUtil.RunAsWork<Object>() {
            @Override
            public Object doWork() throws Exception {
                try {
                    transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Object>() {
                        @Override
                        public Object execute() throws Throwable {
                        final Map<Resource,NodeRef> deployed = bootstrapService.deployResources(
                            workflowLocationPattern,
                            customWorkflowDefsRepositoryLocation,
                            new ContentCompareStrategy(resourceHelper),
                            "UTF-8",
                            MimetypeMap.MIMETYPE_XML,
                            WorkflowModel.TYPE_WORKFLOW_DEF
                        );
                        for (Map.Entry<Resource, NodeRef> entry : deployed.entrySet()) {
                            final NodeRef nodeRef = entry.getValue();
                            if (!Boolean.TRUE.equals(nodeService.getProperty(nodeRef, WorkflowModel.PROP_WORKFLOW_DEF_DEPLOYED))) {
                                final Resource workflowDefinition = entry.getKey();
                                final String fileName = workflowDefinition.getFilename();
                                nodeService.setProperty(nodeRef, WorkflowModel.PROP_WORKFLOW_DEF_ENGINE_ID, "activiti");
                                nodeService.setProperty(nodeRef, WorkflowModel.PROP_WORKFLOW_DEF_NAME, fileName);
                                nodeService.setProperty(nodeRef, WorkflowModel.PROP_WORKFLOW_DEFINITION_NAME, fileName);
                                nodeService.addAspect(nodeRef, ContentModel.ASPECT_TITLED, ImmutableMap.<QName, Serializable>builder()
                                    .put(ContentModel.PROP_TITLE, fileName)
                                    .build()
                                );
                                nodeService.setProperty(nodeRef, WorkflowModel.PROP_WORKFLOW_DEF_DEPLOYED, true);
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
        }, AuthenticationUtil.getSystemUserName());
    }
}
