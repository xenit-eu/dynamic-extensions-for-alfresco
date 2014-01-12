package com.github.dynamicextensionsalfresco.gradle

/**
 * Client for performing operations against the REST API
 */
class BundleService {

	RestClient client = new RestClient()
	
	String apiPath = "/dynamic-extensions/api"

	def installBundle(File file) {
		if (!file.exists()) {
			throw new FileNotFoundException(file.path)
		}
		if (!file.name.matches(/.+\.jar$/)) {
			throw new IllegalArgumentException("Not a JAR file: ${file.absolutePath}.");
		}
		client.postFile(path: "$apiPath/bundles", file: file, mimeType: "application/java-archive")
	}
}
