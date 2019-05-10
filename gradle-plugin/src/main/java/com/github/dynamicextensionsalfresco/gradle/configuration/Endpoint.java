package com.github.dynamicextensionsalfresco.gradle.configuration;

import java.net.MalformedURLException;
import java.net.URL;
import javax.inject.Inject;
import org.gradle.api.GradleException;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.provider.ProviderFactory;

/**
 * @author Laurent Van der Linden
 */
public class Endpoint {

    private final Property<String> host;
    private final Property<Integer> port;
    private final Property<String> serviceUrl;
    private final Property<String> protocol;

    private final Provider<URL> url;

    @Inject
    public Endpoint(ObjectFactory objectFactory, ProviderFactory providerFactory) {
        host = objectFactory.property(String.class);
        port = objectFactory.property(Integer.class);
        serviceUrl = objectFactory.property(String.class);
        protocol = objectFactory.property(String.class);

        host.set("localhost");
        port.set(8080);
        serviceUrl.set("/alfresco/service");
        protocol.set("http");

        url = providerFactory.provider(() -> {
            try {
                return new URL(protocol.get(), host.get(), port.get(), serviceUrl.get());
            } catch (MalformedURLException e) {
                throw new GradleException("Invalid Alfresco endpoint configuration", e);
            }
        });
    }

    public Property<String> getHost() {
        return host;
    }

    public Property<Integer> getPort() {
        return port;
    }

    public Property<String> getServiceUrl() {
        return serviceUrl;
    }

    public Property<String> getProtocol() {
        return protocol;
    }

    public Provider<URL> getUrl() {
        return url;
    }

    public void setPort(int port) {
        getPort().set(port);
    }

    public void setPort(String port) {
        setPort(Integer.parseInt(port));
    }
}
