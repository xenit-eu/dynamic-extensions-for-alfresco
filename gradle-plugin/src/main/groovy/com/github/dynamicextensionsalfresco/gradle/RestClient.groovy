package com.github.dynamicextensionsalfresco.gradle

import com.github.dynamicextensionsalfresco.gradle.configuration.Authentication
import com.github.dynamicextensionsalfresco.gradle.configuration.Endpoint
import groovy.json.JsonSlurper

/**
 * Client for performing REST operations on the Alfresco service layer.
 * <p>
 * This implementation relies on java.net classes.
 */
class RestClient {

    Endpoint endpoint = new Endpoint()
    Authentication authentication = new Authentication()

    def postFile(Map options) {
        return connect(options, 'POST', { conn ->
            conn.doOutput = true

            options.file.withInputStream { data -> conn.outputStream << data }
            conn.outputStream.flush()
        })
    }

    def post(Map options) {
        return connect(options, 'POST', { conn ->
            conn.doOutput = true

            if (options.body) conn.outputStream << options.body
            conn.outputStream.flush()
        })
    }

    def get(Map options) {
        return connect(options, 'GET')
    }

    def connect(Map options, String method, Closure onConnect = null) {
        if (!options.path) throw new NullPointerException("A url path should be provided")
        HttpURLConnection conn = new URL("${endpoint.url}/${options.path.replaceAll(/^\//, "")}").openConnection()
        conn.method = method
        Authentication authentication = options.authentication ?: authentication
        if (authentication) {
            conn.setRequestProperty("Authorization", "Basic ${authentication.basic}")
        }

        if (options.mimeType) {
            conn.setRequestProperty("Content-Type", options.mimeType)
        }

        try {
            if (onConnect) onConnect(conn)

            return new JsonSlurper().parseText(conn.content.text)
        } catch (e) {
            String message = e.message;
            def errorText = conn.errorStream?.text
            if (errorText) {
                message = errorText;
            }
            if (conn.getHeaderField('Content-Type')?.contains('application/json')) {
                def json = new JsonSlurper().parseText(errorText)
                if (json.message) {
                    message = json.message
                }
            }
            throw new RestClientException([status: [code: conn.responseCode, message: conn.responseMessage ], message: message])
        }
    }
}


class RestClientException extends RuntimeException {
	
	Map status = [:] 
	String message
	
}
