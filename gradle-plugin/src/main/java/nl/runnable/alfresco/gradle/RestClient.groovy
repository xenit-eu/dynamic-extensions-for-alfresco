package nl.runnable.alfresco.gradle

import java.net.*
import groovy.json.*

/**
 * Client for performing REST operations on the Alfresco service layer.
 * <p>
 * This implementation relies on java.net classes.
 */
class RestClient {

	Endpoint endpoint = new Endpoint()
	Authentication authentication = new Authentication()

	def postFile(Map options) {
		URLConnection conn = new URL("${endpoint.url}/${options.path.replaceAll(/^\//, "")}").openConnection()
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
		String json = conn.content.text
		return new JsonSlurper().parseText(json)
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
