package nl.runnable.alfresco.repository.dictionary;

import java.io.IOException;

import org.alfresco.repo.dictionary.M2Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

/**
 * {@link FactoryBean} for creating {@link M2Model}s.
 * 
 * @author Laurens Fridael
 * 
 */
public class M2ModelFactoryBean implements FactoryBean<M2Model> {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	/* Configuration */

	private Resource model;

	/* State */

	private M2Model m2Model;

	@Override
	public Class<? extends M2Model> getObjectType() {
		return M2Model.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public M2Model getObject() throws IOException {
		Assert.state(getModel() != null, "ModelMetadata has not been configured.");

		if (m2Model == null) {
			m2Model = createM2Model();
		}
		return m2Model;
	}

	protected M2Model createM2Model() throws IOException {
		final Resource model = getModel();
		if (logger.isDebugEnabled()) {
			logger.debug("Creating M2Model from {}", model);
		}
		return M2Model.createModel(model.getInputStream());
	}

	/* Configuration */

	@Required
	public void setModel(final Resource model) {
		this.model = model;
	}

	public Resource getModel() {
		return model;
	}

}
