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
    private final Versions versions;

    @Inject
    public BaseConfig(ObjectFactory objectFactory) {
        repository = objectFactory.newInstance(Repository.class);
        versions = objectFactory.newInstance(Versions.class);
    }

    public Repository getRepository() {
        return repository;
    }

    public Versions getVersions() {
        return versions;
    }

    public void repository(Action<? super Repository> action) {
        action.execute(repository);
    }

    public void versions(Action<? super Versions> action) {
        action.execute(versions);
    }
}
