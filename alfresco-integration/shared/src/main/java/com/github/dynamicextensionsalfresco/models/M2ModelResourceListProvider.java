package com.github.dynamicextensionsalfresco.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.dictionary.M2Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.Assert;

public class M2ModelResourceListProvider implements M2ModelListProvider, ResourceLoaderAware {

    private final Logger logger = LoggerFactory.getLogger(M2ModelResourceListProvider.class);

    private final String modelLocationPattern = "osgibundle:/META-INF/alfresco/models/*.xml";

    protected ResourcePatternResolver resourcePatternResolver;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        Assert.isInstanceOf(ResourcePatternResolver.class, resourceLoader);
        this.resourcePatternResolver = (ResourcePatternResolver) resourceLoader;
    }


    private List<M2ModelResource> _models;

    @Override
    public List<M2ModelResource> getModels() {
        if (this._models == null) {
            synchronized(this)
            {
                if (this._models==null)
                    this._models = this.createModels();
            }
        }
        return this._models;
    }

    protected List<M2ModelResource> createModels() {
        List<M2ModelResource> models = new ArrayList<>();
        try {
            for (Resource resource : resourcePatternResolver.getResources(modelLocationPattern)) {
                try {
                    M2Model model = createM2Model(resource);
                    models.add(new M2ModelResource(resource, model));
                } catch (Exception e) {
                    logger.warn("Could not create model from {}: {}", resource, e.getMessage());
                }

            }
        } catch (IOException e) {
            throw new AlfrescoRuntimeException("Failed to create model", e);
        }
        return models;
    }

    protected M2Model createM2Model(Resource resource) throws IOException {
        return M2Model.createModel(resource.getInputStream());
    }

}
