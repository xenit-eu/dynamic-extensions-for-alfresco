package com.github.dynamicextensionsalfresco.models;

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
public class M2ModelListFactoryBean implements FactoryBean<List<M2ModelResource>>, ResourceLoaderAware {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	/* Dependencies */

	private ResourcePatternResolver resourcePatternResolver;

	/* Configuration */

    private static final String modelLocationPattern = "osgibundle:/META-INF/alfresco/models/*.xml";

	/* State */

	private List<M2ModelResource> models;

	/* Operations */

	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends List<M2ModelResource>> getObjectType() {
		return (Class<? extends List<M2ModelResource>>) ((Class<?>)List.class);
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public List<M2ModelResource> getObject() throws IOException {
		if (models == null) {
			models = createModels();
		}
		return models;
	}

	protected List<M2ModelResource> createModels() throws IOException {
		final List<M2ModelResource> models = new ArrayList<M2ModelResource>();
		for (final Resource resource : getResourcePatternResolver().getResources(modelLocationPattern)) {
			try {
				final M2Model model = createM2Model(resource);
				models.add(new M2ModelResource(resource, model));
			} catch (final Exception e) {
				if (logger.isWarnEnabled()) {
					logger.warn("Could not create model from {}: {}", resource, e.getMessage());
				}
			}
		}
		return models;
	}

    protected M2Model createM2Model(Resource resource) throws IOException {
        return M2Model.createModel(resource.getInputStream());
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
}
