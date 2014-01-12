package com.github.dynamicextensionsalfresco.policy;

import org.alfresco.service.namespace.NamespaceException;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.NamespaceService;

import java.util.Collection;

/**
 * Needed by {@link AnnotationBasedBehaviourRegistrar} for prefix resolving.
 *
 * @author Laurent Van der Linden
 */
public class StaticNamespacePrefixResolver implements NamespacePrefixResolver {
	@Override
	public String getNamespaceURI(String s) throws NamespaceException {
		return NamespaceService.CONTENT_MODEL_1_0_URI;
	}

	@Override
	public Collection<String> getPrefixes(String s) throws NamespaceException {
		return null;
	}

	@Override
	public Collection<String> getPrefixes() {
		return null;
	}

	@Override
	public Collection<String> getURIs() {
		return null;
	}
}
