package com.github.dynamicextensionsalfresco.models

import org.alfresco.repo.dictionary.DictionaryDAO
import org.alfresco.repo.dictionary.M2Model
import org.alfresco.service.namespace.QName
import org.slf4j.LoggerFactory
import java.util.*

/**
 * Registers and unregisters [M2Model]s with a [DictionaryDAO]. This class is stateful and intended to
 * support dynamic loading of models.

 * @author Laurens Fridael
 */
public open class DAOModelRegistrar(private val dictionaryDao: DictionaryDAO, modelsToRegister: M2ModelListProvider) : AbstractModelRegistrar(modelsToRegister) {

    private val logger = LoggerFactory.getLogger(javaClass)

    /* State */

    private val registeredModelNames = LinkedList<QName>()

    /* Main operations */

    override fun unregisterModels() {
        val it = registeredModelNames.iterator()
        while (it.hasNext()) {
            val modelName = it.next()
            try {
                logger.debug("Unregistering model '{}' via DictionaryDAO", modelName)
                dictionaryDao.removeModel(modelName)
            } finally {
                it.remove()
            }
        }
    }

    override fun registerModel(modelResource: M2ModelResource) {
        val qName = dictionaryDao.putModel(modelResource.m2Model)
        registeredModelNames.add(qName)
    }
}
