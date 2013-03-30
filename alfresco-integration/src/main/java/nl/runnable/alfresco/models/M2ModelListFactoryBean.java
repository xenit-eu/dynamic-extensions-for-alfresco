package nl.runnable.alfresco.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.repo.dictionary.M2Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * {@link FactoryBean} for creating multiple {@link M2Model}s from {@link Resource}s matching a given location pattern.
 * 
 * @author Laurens Fridael
 * 
 */
public class M2ModelListFactoryBean implements FactoryBean<List<M2Model>>, ResourceLoaderAware {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	/* Dependencies */

	private ResourcePatternResolver resourcePatternResolver;

	/* Configuration */

	private String locationPattern;

	/* State */

	private List<M2Model> models;

	/* Construction */

	public M2ModelListFactoryBean(final String locationPattern) {
		this.locationPattern = locationPattern;
	}

	public M2ModelListFactoryBean() {
	}

	/* Operations */

	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends List<M2Model>> getObjectType() {
		return (Class<? extends List<M2Model>>) ((Class<?>)List.class);
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public List<M2Model> getObject() throws IOException {
		Assert.state(StringUtils.hasText(locationPattern), "Location pattern has not been configured.");

		if (models == null) {
			models = createModels();
		}
		return models;
	}

	protected List<M2Model> createModels() throws IOException {
		final List<M2Model> models = new ArrayList<M2Model>();
		for (final Resource resource : getResourcePatternResolver().getResources(locationPattern)) {
			try {
				final M2Model model = createM2Model(resource);
				models.add(model);
			} catch (final Exception e) {
				if (logger.isWarnEnabled()) {
					logger.warn("Could not create model from {}: {}", resource, e.getMessage());
				}
			}
		}
		return models;
	}

	protected M2Model createM2Model(final Resource resource) throws IOException {
		final M2ModelFactoryBean factoryBean = new M2ModelFactoryBean();
		factoryBean.setModel(resource);
		return factoryBean.getObject();
	}

	/* Dependencies */

	@Override
	public void setResourceLoader(final ResourceLoader resourceLoader) {
		Assert.isInstanceOf(ResourcePatternResolver.class, resourceLoader);
		resourcePatternResolver = (ResourcePatternResolver) resourceLoader;
	}

	protected ResourcePatternResolver getResourcePatternResolver() {
		return resourcePatternResolver;
	}

	/* Configuration */

	public void setLocationPattern(final String locationPattern) {
		this.locationPattern = locationPattern;
	}

	public String getLocationPattern() {
		return locationPattern;
	}

}
