package com.github.dynamicextensionsalfresco.gradle.configuration;


import javax.inject.Inject;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;

/**
 * Default versions for the various dependencies.
 */

public class Versions {
	private Property<String> dynamicExtensions;

	private Property<String> surf;

	private Property<String> spring;

	@Inject
	public Versions(ObjectFactory objectFactory) {
		dynamicExtensions = objectFactory.property(String.class);
		surf = objectFactory.property(String.class);
		spring = objectFactory.property(String.class);
	}

	public Property<String> getDynamicExtensions() {
		return dynamicExtensions;
	}

	public Property<String> getSurf() {
		return surf;
	}

	public Property<String> getSpring() {
		return spring;
	}
}
