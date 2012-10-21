package nl.runnable.alfresco.repository.dictionary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import nl.runnable.alfresco.metadata.ExtensionMetadata;
import nl.runnable.alfresco.metadata.ModelMetadata;

import org.alfresco.repo.dictionary.DictionaryDAO;
import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.service.cmr.dictionary.DictionaryException;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

/**
 * Registers and unregisters {@link M2Model}s with a {@link DictionaryDAO}. This class is stateful and intended to
 * support dynamic loading of models.
 * 
 * @author Laurens Fridael
 * 
 */
public class ModelRegistrar {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	/* Dependencies */

	private DictionaryDAO dictionaryDao;

	// Note: dependency is optional.
	private ExtensionMetadata metadata;

	/* Configuration */

	private List<M2Model> models = Collections.emptyList();

	/* State */

	private final List<QName> registeredModelNames = new ArrayList<QName>();

	/* Operations */

	public void registerModels() {
		for (final M2Model model : getModels()) {
			try {
				if (logger.isDebugEnabled()) {
					logger.debug("Registering model '{}'", model.getName());
				}
				final QName modelName = getDictionaryDao().putModel(model);
				registeredModelNames.add(modelName);
				registerModelMetadata(modelName, model);
			} catch (final DictionaryException e) {
				if (logger.isWarnEnabled()) {
					logger.warn("Could not register model '{}': ", model.getName(), e.getMessage());
				}
			}
		}
	}

	public void unregisterModels() {
		for (final Iterator<QName> it = registeredModelNames.iterator(); it.hasNext();) {
			final QName modelName = it.next();
			try {
				if (logger.isDebugEnabled()) {
					logger.debug("Unregistering model: '{}'", modelName);
				}
				getDictionaryDao().removeModel(modelName);
				unregisterMetadata(modelName);
			} finally {
				it.remove();
			}
		}
	}

	/* Utility operations */

	protected void registerModelMetadata(final QName qName, final M2Model m2Model) {
		if (getMetadata() == null) {
			return;
		}
		final ModelMetadata model = new ModelMetadata();
		model.setName(m2Model.getName());
		model.setVersion(m2Model.getVersion());
		model.setDescription(m2Model.getDescription());
		getMetadata().registerModel(qName, model);
	}

	protected void unregisterMetadata(final QName modelName) {
		if (getMetadata() == null) {
			return;
		}
		getMetadata().unregisterModel(modelName);
	}

	/* Dependencies */

	@Required
	public void setDictionaryDao(final DictionaryDAO dictionaryDao) {
		this.dictionaryDao = dictionaryDao;
	}

	protected DictionaryDAO getDictionaryDao() {
		return dictionaryDao;
	}

	public void setMetadata(final ExtensionMetadata metadata) {
		this.metadata = metadata;
	}

	protected ExtensionMetadata getMetadata() {
		return metadata;
	}

	/* Configuration */

	public void setModels(final List<M2Model> models) {
		Assert.notNull(models, "Models cannot be null.");
		this.models = models;
	}

	public List<M2Model> getModels() {
		return models;
	}

	/* State */

	protected List<QName> getRegisteredModelNames() {
		return registeredModelNames;
	}

}
