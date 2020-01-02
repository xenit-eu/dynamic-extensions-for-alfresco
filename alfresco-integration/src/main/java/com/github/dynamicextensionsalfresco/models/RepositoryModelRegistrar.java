package com.github.dynamicextensionsalfresco.models;


import com.github.dynamicextensionsalfresco.resources.ResourceHelper;
import org.alfresco.repo.dictionary.RepositoryLocation;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.admin.RepoAdminService;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.transaction.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Register models in the repository as cm:dictionaryModel's using the {@link RepoAdminService}.
 */
public class RepositoryModelRegistrar extends AbstractModelRegistrar {

    private Logger logger = LoggerFactory.getLogger(RepositoryModelRegistrar.class);

    @Autowired
    public RepositoryLocation customModelsRepositoryLocation;
    @Autowired
    public NodeService nodeService;
    @Autowired
    public RepoAdminService repoAdminService;
    @Autowired
    public TransactionService transactionService;
    @Autowired
    public ResourceHelper resourceHelper;

    @Override
    public void unregisterModels() {
        // we do not unregister models as this can break existing content/index integrity
    }

    @Override
    public void registerModel(final M2ModelResource modelResource) {
        AuthenticationUtil.runAsSystem(() -> {
            try {
                transactionService.getRetryingTransactionHelper().doInTransaction(() -> {
                    try {
                        if (resourceHelper.nodeDiffersFromResource(modelResource.getResource(), customModelsRepositoryLocation)) {
                            repoAdminService.deployModel(
                                    modelResource.getResource().getInputStream(),
                                    modelResource.getResource().getFilename());
                            logger.debug("Registered model {}", modelResource.getName());
                        }
                    }
                    catch (Exception e){
                        logger.error("Failed to deploy M2Model {} as a cm:dictionaryModel",
                                modelResource.getName(), e);
                    }
                    return null;
                },false,false);
                } catch(Exception e){
                    logger.error("tx error", e);
                }
                return null;
            });
        }
    }
