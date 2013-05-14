package nl.runnable.alfresco.models;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.dictionary.RepositoryLocation;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.admin.RepoAdminService;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.ISO9075;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

import static org.alfresco.repo.admin.RepoAdminServiceImpl.defaultSubtypeOfDictionaryModel;

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

	/* Main operations */

	public void unregisterModels() {
		// we do not unregister models as this can break existing content/index integrity
	}

	@Override
	protected void registerModel(final M2ModelResource modelResource) {
		AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Object>() {
			@Override
			public Object doWork() throws Exception {
				try {
					transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Object>() {
						@Override
						public Object execute() throws Throwable {
							try {
								if (existingModelDiffers(modelResource)) {
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
		});
	}

	private boolean existingModelDiffers(M2ModelResource modelResource) throws IOException {
		StoreRef storeRef = customModelsRepositoryLocation.getStoreRef();
		NodeRef rootNode = nodeService.getRootNode(storeRef);
		List<NodeRef> nodeRefs = searchService.selectNodes(rootNode,
				String.format("%s/cm:%s[%s]",
						customModelsRepositoryLocation.getPath(), ISO9075.encode(modelResource.getResource().getFilename()),
						defaultSubtypeOfDictionaryModel
				), null, namespaceService, false);
		if (nodeRefs.isEmpty() == false) {
			final NodeRef modelRef = nodeRefs.get(0);
			final ContentReader reader = contentService.getReader(modelRef, ContentModel.PROP_CONTENT);
			if (IOUtils.contentEquals(reader.getContentInputStream(), modelResource.getResource().getInputStream())) {
				logger.debug("Existing repo model is up to date.");
				return false;
			}
		}
		return true;
	}

	@Autowired
	public void setModelsFactroy(M2ModelListFactoryBean m2ModelListFactoryBean) {
		try {
			super.setModels(m2ModelListFactoryBean.getObject());
		} catch (IOException e) {
			logger.error("Failed to get list of Document models.", e);
		}
	}
}
