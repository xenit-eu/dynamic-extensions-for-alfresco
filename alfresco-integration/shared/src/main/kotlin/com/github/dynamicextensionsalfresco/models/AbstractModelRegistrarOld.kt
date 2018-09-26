//package com.github.dynamicextensionsalfresco.models
//
//import com.github.dynamicextensionsalfresco.osgi.DependencyMetadataProvider
//import com.github.dynamicextensionsalfresco.osgi.DependencySorter
//import org.alfresco.service.cmr.dictionary.DictionaryException
//import org.slf4j.LoggerFactory
//import org.springframework.context.ResourceLoaderAware
//import org.springframework.core.io.ResourceLoader
//import org.springframework.core.io.support.ResourcePatternResolver
//
///**
// * Based class for Model registrars. Registers Models by dependency order.
//
// * @author Laurent Van der Linden
// */
//public abstract class AbstractModelRegistrarOld(val modelsToRegister: M2ModelListProvider) : ModelRegistrar, ResourceLoaderAware, DependencyMetadataProvider<M2ModelResource> {
//    private val logger = LoggerFactory.getLogger(AbstractModelRegistrar::class.java)
//
//    /* Configuration */
//
//    protected var resourcePatternResolver: ResourcePatternResolver? = null
//        private set
//
//    override fun registerModels() {
//        val sortedModels = DependencySorter.sort(modelsToRegister.models, this)
//
//        for (modelResource in sortedModels) {
//            try {
//                registerModel(modelResource)
//            } catch (e: DictionaryException) {
//                if (logger.isWarnEnabled) {
//                    logger.warn("Could not register model '${modelResource.name}'", e)
//                }
//            }
//        }
//    }
//
//    override fun imports(item: M2ModelResource): Collection<Any> {
//        return item.m2Model.imports.map { it.uri }
//    }
//
//    override fun exports(item: M2ModelResource): Collection<Any> {
//        return item.m2Model.namespaces.map { it.uri }
//    }
//
//    override fun allowCircularReferences(): Boolean{
//        return false
//    }
//
//
//    protected abstract fun registerModel(modelResource: M2ModelResource)
//
//    /* Configuration */
//
//    override fun setResourceLoader(resourceLoader: ResourceLoader) {
//        this.resourcePatternResolver = resourceLoader as ResourcePatternResolver
//    }
//}
