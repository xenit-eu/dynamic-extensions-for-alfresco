package com.github.dynamicextensionsalfresco.gradle.configuration;

import javax.inject.Inject;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.model.ObjectFactory;

/**
 * @author Laurent Van der Linden
 */
public class BaseConfig {
    private final Repository repository;

    @Inject
    public BaseConfig(ObjectFactory objectFactory) {
        repository = objectFactory.newInstance(Repository.class);
    }

    public Repository getRepository() {
        return repository;
    }

    public void repository(Action<? super Repository> action) {
        action.execute(repository);
    }

}
