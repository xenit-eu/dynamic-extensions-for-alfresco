package com.github.dynamicextensionsalfresco.models

import com.github.dynamicextensionsalfresco.warn
import org.alfresco.repo.dictionary.M2Model
import org.slf4j.LoggerFactory
import org.springframework.context.ResourceLoaderAware
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.core.io.support.ResourcePatternResolver
import org.springframework.util.Assert
import java.util.*

interface M2ModelListProvider {
    val models: List<M2ModelResource>
}
/**
 * [FactoryBean] for creating multiple [M2Model]s from [Resource]s matching a given location pattern.

 * @author Laurens Fridael
 */
class M2ModelResourceListProvider : M2ModelListProvider, ResourceLoaderAware {
    private val logger = LoggerFactory.getLogger(javaClass)

    private val modelLocationPattern = "osgibundle:/META-INF/alfresco/models/*.xml"

    protected var resourcePatternResolver: ResourcePatternResolver? = null
        private set

    override val models: List<M2ModelResource> by lazy { createModels() }

    protected fun createModels(): List<M2ModelResource> {
        val models = ArrayList<M2ModelResource>()
        for (resource in resourcePatternResolver!!.getResources(modelLocationPattern)) {
            try {
                val model = createM2Model(resource)
                models.add(M2ModelResource(resource, model))
            } catch (e: Exception) {
                logger.warn {
                    "Could not create model from ${resource}: ${e.message}}"
                }
            }

        }
        return models
    }

    protected fun createM2Model(resource: Resource): M2Model {
        return M2Model.createModel(resource.inputStream)
    }

    override fun setResourceLoader(resourceLoader: ResourceLoader) {
        Assert.isInstanceOf(ResourcePatternResolver::class.java, resourceLoader)
        resourcePatternResolver = resourceLoader as ResourcePatternResolver
    }
}
