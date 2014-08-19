package com.github.dynamicextensionsalfresco.models;

import com.github.dynamicextensionsalfresco.resources.ResourceHelper;
import org.alfresco.repo.dictionary.RepositoryLocation;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.admin.RepoAdminService;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.transaction.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Register models in the repository as cm:dictionaryModel's using the {@link RepoAdminService}.
 *
 * @author Laurent Van der Linden
 */
public class RepositoryModelRegistrar extends AbstractModelRegistrar {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	/* Dependencies */

	@Autowired
	private RepoAdminService repoAdminService;

	@Autowired
	protected RepositoryLocation customModelsRepositoryLocation;

	@Autowired
	protected SearchService searchService;

	@Autowired
	protected NodeService nodeService;

	@Autowired
	protected NamespaceService namespaceService;

	@Autowired
	protected ContentService contentService;

	@Autowired
	protected TransactionService transactionService;

    @Autowired
    protected ResourceHelper resourceHelper;
	/* Main operations */

	public void unregisterModels() {
		// we do not unregister models as this can break existing content/index integrity
	}

	@Override
	protected void registerModel(final M2ModelResource modelResource) {
		AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Object>() {
			@Override
			public Object doWork() throws Exception {
				try {
					transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Object>() {
						@Override
						public Object execute() throws Throwable {
							try {
								if (resourceHelper.nodeDiffersFromResource(modelResource.getResource(), customModelsRepositoryLocation)) {
									repoAdminService.deployModel(
											modelResource.getResource().getInputStream(),
											modelResource.getResource().getFilename()
									);
									logger.debug(String.format("Registered model %s", modelResource.getName()));
								}
							} catch (Exception e) {
								logger.error(String.format("Failed to deploy M2Model %s as a cm:dictionaryModel", modelResource.getName()), e);
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
