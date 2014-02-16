package com.github.dynamicextensionsalfresco.gradle

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
		HttpURLConnection conn = new URL("${endpoint.url}/${options.path.replaceAll(/^\//, "")}").openConnection()
		conn.method = "POST"
		Authentication authentication = options.authentication ?: authentication
		if (authentication) {
			conn.setRequestProperty("Authorization", "Basic ${authentication.basic}")
		}
		conn.setRequestProperty("Content-Length", "${options.file.length()}")
		conn.setRequestProperty("Content-Type", options.mimeType)
		conn.doOutput = true
		options.file.withInputStream { data -> conn.outputStream << data }
		conn.outputStream.flush()
		try {
			return new JsonSlurper().parseText(conn.content.text)
		} catch (e) {
			String message = e.message;
			if (conn.getHeaderField('Content-Type').contains('application/json')) {
				def json = new JsonSlurper().parseText(conn.errorStream.text)
				message = json.message
			}
			throw new RestClientException([status: [code: conn.responseCode, message: conn.responseMessage ], message: message]) 
		}
	}
}

class Authentication {
	
	String username
	String password

	boolean asBoolean() {
		(username && password)
	}

	String getBasic() {
		// Use toString() as a workaround for http://jira.codehaus.org/browse/GROOVY-5761
		"$username:$password".toString().bytes.encodeBase64()
	}
}

class Endpoint {
	
	static Map DEFAULTS = [host: "localhost", port: "8080", servicePath: "/alfresco/service"]
	
	String host = DEFAULTS.host
	String port = DEFAULTS.port
	String servicePath = DEFAULTS.servicePath

	URL getUrl() {
		new URL("http://$host:$port/${servicePath.replaceAll(/^\//, "")}")
	}
}

class RestClientException extends RuntimeException {
	
	Map status = [:] 
	String message
	
}
