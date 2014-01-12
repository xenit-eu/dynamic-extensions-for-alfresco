package com.github.dynamicextensionsalfresco.blueprint;

import org.springframework.beans.factory.xml.NamespaceHandler;
import org.springframework.beans.factory.xml.NamespaceHandlerResolver;

/**
 * {@link NamespaceHandlerResolver} for internal use by {@link DynamicExtensionsApplicationContext}
 * 
 * @author Laurens Fridael
 * 
 */
class CompositeNamespaceHandlerResolver implements NamespaceHandlerResolver {

	private final NamespaceHandlerResolver[] resolvers;

	CompositeNamespaceHandlerResolver(final NamespaceHandlerResolver... namespaceHandlerResolvers) {
		this.resolvers = namespaceHandlerResolvers;
	}

	@Override
	public NamespaceHandler resolve(final String namespaceUri) {
		for (final NamespaceHandlerResolver resolver : resolvers) {
			final NamespaceHandler handler = resolver.resolve(namespaceUri);
			if (handler != null) {
				return handler;
			}
		}
		return null;
	}

}
