package com.github.dynamicextensionsalfresco.models;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import org.alfresco.repo.dictionary.DictionaryDAO;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Registers and unregisters [M2Model]s with a [DictionaryDAO]. This class is stateful and intended to
 * support dynamic loading of models.
 *
 * @author Laurens Fridael
 * @author Toon Geens
 */

public class DAOModelRegistrar extends AbstractModelRegistrar {

    private final Logger logger = LoggerFactory.getLogger(DAOModelRegistrar.class);

    private final DictionaryDAO dictionaryDao;

    /* State */
    private final List<QName> registeredModelNames = new LinkedList<>();

    public DAOModelRegistrar(DictionaryDAO dictionaryDao, M2ModelListProvider modelsToRegister) {
        super(modelsToRegister);
        this.dictionaryDao = dictionaryDao;
    }

    @Override
    protected void registerModel(M2ModelResource modelResource) {
        QName qname = dictionaryDao.putModel(modelResource.getM2Model());
        registeredModelNames.add(qname);
    }

    @Override
    public void unregisterModels() {
        Iterator<QName> it = this.registeredModelNames.iterator();
        while (it.hasNext()) {
            QName modelName = it.next();
            try {
                logger.debug("Unregistering model '{}' via DictionaryDAO", modelName);
                dictionaryDao.removeModel(modelName);
            } finally {
                it.remove();
            }
        }
    }
}
