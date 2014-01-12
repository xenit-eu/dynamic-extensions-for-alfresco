package com.github.dynamicextensionsalfresco.osgi.spring;

import org.springframework.beans.factory.xml.PluggableSchemaResolver;

/**
 * TODO: Describe the classloading implications. Quite important to communicate the intention behind this.
 * 
 * @author Laurens Fridael
 * 
 */
public class EmbeddingApplicationSchemaResolver extends PluggableSchemaResolver {

	public EmbeddingApplicationSchemaResolver() {
		super(EmbeddingApplicationSchemaResolver.class.getClassLoader());
	}
}
