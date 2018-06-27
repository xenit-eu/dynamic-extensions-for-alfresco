package com.github.dynamicextensionsalfresco.osgi.spring;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

import com.github.dynamicextensionsalfresco.osgi.ServiceDefinition;
import com.github.dynamicextensionsalfresco.osgi.ServiceDefinitionEditor;

import org.apache.commons.collections.list.AbstractLinkedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.io.Resource;

/**
 * {@link FactoryBean} for creating a List of {@link ServiceDefinition}s from a text file.
 * 
 * @author Laurens Fridael
 * 
 */
public class ServiceDefinitionConfigurationFactoryBean extends
		AbstractConfigurationFileFactoryBean<List<ServiceDefinition>> {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	/* State */

	private List<ServiceDefinition> serviceDefinitions;

	/* Operations */

	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends List<ServiceDefinition>> getObjectType() {
		return (Class<? extends List<ServiceDefinition>>) (Class<?>) List.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public List<ServiceDefinition> getObject() throws IOException {
		if (serviceDefinitions == null) {
			serviceDefinitions = createServiceDefinitions();
		}
		return serviceDefinitions;
	}

	/* Utility operations */

	protected List<ServiceDefinition> createServiceDefinitions() throws IOException {
		final List<ServiceDefinition> serviceDefinitions = new ArrayList<ServiceDefinition>();
		final ServiceDefinitionEditor serviceDefinitionEditor = new ServiceDefinitionEditor();
		for (final Resource configuration : resolveConfigurations()) {
			final LineNumberReader in = new LineNumberReader(new InputStreamReader(configuration.getInputStream()));
			for (String line; (line = in.readLine()) != null;) {
				line = line.trim();
				if (line.isEmpty() || line.startsWith("#")) {
					// Skip empty lines and comments.
					continue;
				}
				try {
					serviceDefinitionEditor.setAsText(line);
					final ServiceDefinition serviceDefinition = (ServiceDefinition) serviceDefinitionEditor.getValue();
					serviceDefinitions.add(serviceDefinition);
				} catch (final IllegalArgumentException e) {
					logger.warn("Could not parse SystemPackage configuration line: {}", e.getMessage());
				}

			}
		}
		return serviceDefinitions;
	}

}