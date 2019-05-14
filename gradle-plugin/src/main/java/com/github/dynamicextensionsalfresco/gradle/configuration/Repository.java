package com.github.dynamicextensionsalfresco.gradle.configuration;

import javax.inject.Inject;
import org.gradle.api.Action;
import org.gradle.api.model.ObjectFactory;

/**
 * @author Laurent Van der Linden
 */
public class Repository {
    private final Authentication authentication;
    private final Endpoint endpoint;

    @Inject
    public Repository(ObjectFactory objectFactory) {
        authentication = objectFactory.newInstance(Authentication.class);
        endpoint = objectFactory.newInstance(Endpoint.class);
    }

    public Authentication getAuthentication() {
        return authentication;
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }

    public void authentication(Action<? super Authentication> action) {
        action.execute(authentication);
    }

    public void endpoint(Action<? super Endpoint> action) {
        action.execute(endpoint);
    }
}
