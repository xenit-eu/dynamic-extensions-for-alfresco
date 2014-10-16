package com.github.dynamicextensionsalfresco.gradle.configuration

/**
 * @author Laurent Van der Linden
 */
class Endpoint {
    String host = "localhost"
    String port = "8080"
    String serviceUrl = "/alfresco/service"
    String protocol = "http"

    URL getUrl() {
        new URL("$protocol://$host:$port/${serviceUrl.replaceAll(/^\//, "")}")
    }
}
