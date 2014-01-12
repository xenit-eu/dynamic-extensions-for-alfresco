package com.github.dynamicextensionsalfresco.blueprint;

import java.io.IOException;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

class CompositeEntityResolver implements EntityResolver {

	private final EntityResolver[] resolvers;

	CompositeEntityResolver(final EntityResolver... resolvers) {
		this.resolvers = resolvers;
	}

	@Override
	public InputSource resolveEntity(final String publicId, final String systemId) throws SAXException, IOException {
		for (final EntityResolver resolver : resolvers) {
			final InputSource inputSource = resolver.resolveEntity(publicId, systemId);
			if (inputSource != null) {
				return inputSource;
			}
		}
		return null;
	}

}
