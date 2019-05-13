package com.github.dynamicextensionsalfresco.gradle.configuration;


import com.github.dynamicextensionsalfresco.gradle.internal.BuildConfig;
import javax.inject.Inject;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;

/**
 * Default versions for the various dependencies.
 */

public class Versions {
	private Property<String> dynamicExtensions;

	@Inject
	public Versions(ObjectFactory objectFactory) {
		dynamicExtensions = objectFactory.property(String.class);
		dynamicExtensions.set(BuildConfig.VERSION);
	}

	public Property<String> getDynamicExtensions() {
		return dynamicExtensions;
	}
}
