package com.github.dynamicextensionsalfresco.models;

import com.github.dynamicextensionsalfresco.osgi.DependencyMetadataProvider;
import com.github.dynamicextensionsalfresco.osgi.DependencySorter;
import org.alfresco.repo.dictionary.M2Namespace;
import org.alfresco.service.cmr.dictionary.DictionaryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by jasper on 17/07/17.
 */
public abstract class AbstractModelRegistrar implements ModelRegistrar, ResourceLoaderAware, DependencyMetadataProvider<M2ModelResource> {

    private Logger logger = LoggerFactory.getLogger(AbstractModelRegistrar.class);
    protected ResourcePatternResolver resourcePatternResolver;

    public AbstractModelRegistrar(){
    }
    public AbstractModelRegistrar(M2ModelListProvider modelsToRegister){
        this.modelsToRegister = modelsToRegister;
    }
    @Autowired
    public void setModelsToRegister(M2ModelListProvider modelsToRegister){
        this.modelsToRegister = modelsToRegister;
    }



    public M2ModelListProvider modelsToRegister;

    private void setResourcePatternResolver(ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
    }


    @Override
    public void registerModels() {
        Collection<M2ModelResource> sortedModels = DependencySorter.sort(modelsToRegister.getModels(), this);
        for (M2ModelResource modelResource:sortedModels) {
            try {
                registerModel(modelResource);
            } catch (DictionaryException e){
                if (logger.isWarnEnabled()) {
                    logger.warn("Could not register model '${modelResource.name}'", e);
                }
            }
        }
    }
    @Override
    public Collection<Object> imports(M2ModelResource item){
        Collection<Object> ret = new ArrayList<Object>();
        for(M2Namespace it:item.getM2Model().getImports())
           ret.add(it.getUri());
        return ret;
    }
    protected abstract void registerModel(M2ModelResource modelResource);


    @Override
    public Collection<Object> exports(M2ModelResource item){
        Collection<Object> ret = new ArrayList<Object>();
        for(M2Namespace it:item.getM2Model().getNamespaces())
            ret.add(it.getUri());
        return ret;
    }
    @Override
    public boolean allowCircularReferences(){
        return false;
    }
    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourcePatternResolver = (ResourcePatternResolver) resourceLoader;
    }
}
