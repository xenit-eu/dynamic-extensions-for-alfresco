package com.github.dynamicextensionsalfresco.messages;

import com.github.dynamicextensionsalfresco.resources.BootstrapService;
import com.github.dynamicextensionsalfresco.resources.ContentCompareStrategy;
import com.github.dynamicextensionsalfresco.resources.ResourceHelper;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.dictionary.RepositoryLocation;
import org.alfresco.repo.i18n.MessageService;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.transaction.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static org.alfresco.repo.security.authentication.AuthenticationUtil.runAsSystem;

/**
 * Service that scans a Dynamic Extension for translations in {@link MessagesRegistrar#BUNDLE_MESSAGES}. It stores them
 * in the Data Dictionary and makes them available in the MessageService.
 *
 * @author Thijs Lemmens
 */
public class MessagesRegistrar implements InitializingBean {

    private static final String BUNDLE_MESSAGES = "osgibundle:/META-INF/alfresco/messages/*.properties";
    private static final String MESSAGES_TARGET = "/app:company_home/app:dictionary/app:messages";

    private final static Logger logger = LoggerFactory.getLogger(MessagesRegistrar.class);

    @Autowired
    protected BootstrapService bootstrapService;

    @Autowired @javax.annotation.Resource(name = "messageService")
    protected MessageService messageService;

    @Autowired
    private ResourceHelper resourceHelper;

    @Autowired
    private TransactionService transactionService;

    @Override
    public void afterPropertiesSet() throws Exception {
        runAsSystem(new AuthenticationUtil.RunAsWork<Object>() {
            @Override
            public Object doWork() throws Exception {
                try {
                    transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Object>() {
                        @Override
                        public Object execute() throws Throwable {
                            logger.debug("Deploying messages...");
                            RepositoryLocation repositoryLocation = new RepositoryLocation(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, MESSAGES_TARGET, SearchService.LANGUAGE_XPATH);
                            Map<Resource, NodeRef> resources = bootstrapService.deployResources(BUNDLE_MESSAGES, repositoryLocation, new ContentCompareStrategy(resourceHelper), null, MimetypeMap.MIMETYPE_TEXT_PLAIN, ContentModel.TYPE_CONTENT);
                            Set<String> registered = new HashSet<String>();
                            for (Resource resource : resources.keySet()) {
                                String baseName = resource.getFilename().replaceAll("^([a-z-A-Z]+)(_|.).+$", "$1");
                                String messageBundlePath = StoreRef.STORE_REF_WORKSPACE_SPACESSTORE + repositoryLocation.getPath() + "/cm:" + baseName;
                                if (!registered.contains(baseName)) {
                                    logger.debug("Registering messagebundle {}", messageBundlePath);
                                    messageService.registerResourceBundle(messageBundlePath);
                                    registered.add(baseName);
                                }
                            }
                            logger.debug("Deploying messages done.");

                            // DIRTY HACK. Initialize early, otherwise other code complaints not having a transaction.
                            String message = messageService.getMessage("cm_contentmodel.property.cm_name.title", Locale.US);
                            logger.debug("Fetching failing message {}", message);
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
}
