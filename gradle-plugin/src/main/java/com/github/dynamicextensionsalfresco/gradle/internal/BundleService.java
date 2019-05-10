package com.github.dynamicextensionsalfresco.gradle.internal;

import com.github.dynamicextensionsalfresco.gradle.internal.rest.RestClient;
import com.github.dynamicextensionsalfresco.gradle.internal.rest.RestClientOptions;
import com.github.dynamicextensionsalfresco.gradle.internal.rest.RestClientPostFileOptions;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Client for performing operations against the REST API
 */
public class BundleService {

	private RestClient client;
	
	private String apiPath;

	public BundleService(RestClient client) {
	    this(client, "/dynamic-extensions/api");
	}

	public BundleService(RestClient client, String apiPath) {
		this.client = client;
		this.apiPath = apiPath;
	}

	public Object installBundle(File file) throws IOException {
		if (!file.exists()) {
			throw new FileNotFoundException(file.getPath());
		}
		if(!file.getName().endsWith(".jar")) {
			throw new IllegalArgumentException("Not a JAR file: "+file.getAbsolutePath());
		}
		return client.postFile(new RestClientPostFileOptions(new RestClientOptions(apiPath+"/bundles", "application/java-archive"), file));
	}
}
