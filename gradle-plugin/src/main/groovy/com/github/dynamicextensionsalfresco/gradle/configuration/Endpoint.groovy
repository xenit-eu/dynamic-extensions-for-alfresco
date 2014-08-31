package com.github.dynamicextensionsalfresco.gradle.configuration

/**
 * @author Laurent Van der Linden
 */
class Endpoint {
    String host = "localhost"
    String port = "8080"
    String serviceUrl = "/alfresco/service"

    URL getUrl() {
        new URL("http://$host:$port/${serviceUrl.replaceAll(/^\//, "")}")
    }
}
