package com.github.dynamicextensionsalfresco.gradle.tasks

import com.github.dynamicextensionsalfresco.gradle.RestClient
import groovy.json.JsonOutput
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * @author Laurent Van der Linden
 */
class CallWebScript extends DefaultTask {
    String method = 'GET'
    String url
    Map jsonBody
    String accept = 'text/plain'
    String mimeType = 'application/json'

    @TaskAction
    def callWebScript() {
        if (!url) throw new NullPointerException("no url specified")
        RestClient client = new RestClient()
        client.with {
            endpoint = project.alfrescoDynamicExtensions.repository.endpoint
            authentication = project.alfrescoDynamicExtensions.repository.authentication
        }
        def response
        switch (method) {
            case 'GET':
                project.logger.info("GET -> $url")
                response = client.get([path: url, accept: accept])
                break
            case 'POST':
                final jsonString = jsonBody instanceof String ? jsonBody : JsonOutput.toJson(jsonBody)
                project.logger.info("POST($jsonString) -> $url")
                response = client.post([path: url, body: jsonString, mimeType: mimeType, accept: accept])
                break
            default:
                throw new IllegalArgumentException("'$method' is not a supported HTTP method for this task")
        }
        project.logger.info response.message
    }
}
