//package com.github.dynamicextensionsalfresco.models
//
//import com.github.dynamicextensionsalfresco.resources.ResourceHelper
//import org.alfresco.repo.dictionary.RepositoryLocation
//import org.alfresco.repo.security.authentication.AuthenticationUtil
//import org.alfresco.repo.transaction.RetryingTransactionHelper
//import org.alfresco.service.cmr.admin.RepoAdminService
//import org.alfresco.service.cmr.repository.ContentService
//import org.alfresco.service.cmr.repository.NodeService
//import org.alfresco.service.cmr.search.SearchService
//import org.alfresco.service.namespace.NamespaceService
//import org.alfresco.service.transaction.TransactionService
//import org.slf4j.Logger
//import org.slf4j.LoggerFactory
//import org.springframework.beans.factory.annotation.Autowired
//
///**
// * Register models in the repository as cm:dictionaryModel's using the [RepoAdminService].
//
// * @author Laurent Van der Linden
// */
//class RepositoryModelRegistrarOld @Autowired constructor(
//		val customModelsRepositoryLocation: RepositoryLocation,
//		val nodeService: NodeService,
//		val repoAdminService: RepoAdminService,
//		val transactionService: TransactionService,
//		val resourceHelper: ResourceHelper,
//		modelsToRegister: M2ModelListProvider
//) : AbstractModelRegistrar(modelsToRegister) {
//
//    private val logger = LoggerFactory.getLogger(javaClass)
//
//    override fun unregisterModels() {
//        // we do not unregister models as this can break existing content/index integrity
//    }
//
//    override fun registerModel(modelResource: M2ModelResource) {
//        AuthenticationUtil.runAsSystem {
//            try {
//                transactionService.retryingTransactionHelper.doInTransaction({
//                    try {
//                        if (resourceHelper.nodeDiffersFromResource(modelResource.resource, customModelsRepositoryLocation)) {
//                            repoAdminService.deployModel(
//                                    modelResource.resource.inputStream,
//                                    modelResource.resource.filename)
//                            logger.debug("Registered model ${modelResource.name}")
//                        }
//                    } catch (e: Exception) {
//                        logger.error("Failed to deploy M2Model ${modelResource.name} as a cm:dictionaryModel", e)
//                    }
//                }, false, false)
//            } catch (e: Exception) {
//                logger.error("tx error", e)
//            }
//        }
//    }
//}
