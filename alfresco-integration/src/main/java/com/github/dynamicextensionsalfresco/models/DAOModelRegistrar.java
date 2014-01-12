package com.github.dynamicextensionsalfresco.models;

import org.alfresco.repo.dictionary.DictionaryDAO;
import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Registers and unregisters {@link M2Model}s with a {@link DictionaryDAO}. This class is stateful and intended to
 * support dynamic loading of models.
 * 
 * @author Laurens Fridael
 * 
 */
public class DAOModelRegistrar extends AbstractModelRegistrar {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	/* State */

	private final List<QName> registeredModelNames = new ArrayList<QName>();

	/* Dependencies */

	private DictionaryDAO dictionaryDao;

	/* Main operations */

	@Override
	public void unregisterModels() {
		for (final Iterator<QName> it = registeredModelNames.iterator(); it.hasNext();) {
			final QName modelName = it.next();
			try {
				if (logger.isDebugEnabled()) {
					logger.debug("Unregistering model '{}' via DictionaryDAO", modelName);
				}
				getDictionaryDao().removeModel(modelName);
			} finally {
				it.remove();
			}
		}
	}

	@Override
	protected void registerModel(M2ModelResource modelResource) {
		final QName qName;
		qName = dictionaryDao.putModel(modelResource.getM2Model());
		registeredModelNames.add(qName);
	}

	/* Dependencies */

	@Required
	public void setDictionaryDao(final DictionaryDAO dictionaryDao) {
		this.dictionaryDao = dictionaryDao;
	}

	protected DictionaryDAO getDictionaryDao() {
		return dictionaryDao;
	}
}
