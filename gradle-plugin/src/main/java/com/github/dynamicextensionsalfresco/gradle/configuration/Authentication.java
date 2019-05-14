package com.github.dynamicextensionsalfresco.gradle.configuration;

import java.util.Base64;
import javax.inject.Inject;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.provider.ProviderFactory;

/**
 * @author Laurent Van der Linden
 */
public class Authentication {

    private final Property<String> username;
    private final Property<String> password;

    private final Provider<String> basic;

    @Inject
    public Authentication(ObjectFactory objectFactory, ProviderFactory providerFactory) {
        username = objectFactory.property(String.class);
        password = objectFactory.property(String.class);

        username.set("admin");
        password.set("admin");

        basic = providerFactory.provider(() -> {
            return Base64.getEncoder().encodeToString((username.get()+":"+password.get()).getBytes());
        });
    }

    public Property<String> getUsername() {
        return username;
    }

    public Property<String> getPassword() {
        return password;
    }

    public Provider<String> getBasic() {
        return basic;
    }
}
