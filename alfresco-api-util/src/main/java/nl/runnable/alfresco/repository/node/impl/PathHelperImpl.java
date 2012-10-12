/*
Copyright (c) 2012, Runnable
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
 * Neither the name of Runnable nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package nl.runnable.alfresco.repository.node.impl;

import java.util.Collection;

import nl.runnable.alfresco.repository.node.PathHelper;

import org.alfresco.service.cmr.repository.Path;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

public class PathHelperImpl implements PathHelper {

	private NamespacePrefixResolver namespacePrefixResolver;

	private PathEncoder pathEncoder = new ISO9075PathEncoder();

	@Required
	public void setNamespacePrefixResolver(final NamespacePrefixResolver namespacePrefixResolver) {
		Assert.notNull(namespacePrefixResolver, "NamespacePrefixResolver cannot be null.");
		this.namespacePrefixResolver = namespacePrefixResolver;
	}

	protected NamespacePrefixResolver getNamespacePrefixResolver() {
		return namespacePrefixResolver;
	}

	public void setPathEncoder(final PathEncoder pathEncoder) {
		Assert.notNull(pathEncoder, "PathEncoder cannot be null.");
		this.pathEncoder = pathEncoder;
	}

	protected PathEncoder getPathEncoder() {
		return pathEncoder;
	}

	@Override
	public String convertPathToString(final Path path) {
		Assert.notNull(path, "Path cannot be null.");
		final StringBuilder str = new StringBuilder();
		for (final Path.Element element : path) {
			final Path.ChildAssocElement childAssociationElement = (Path.ChildAssocElement) element;
			if (childAssociationElement.getRef().getParentRef() != null) {
				final QName childQName = childAssociationElement.getRef().getQName();
				final String prefix = getPrefixFor(childQName);
				final String localName = getPathEncoder().encodePath(childQName.getLocalName());
				str.append(String.format("/%s:%s", prefix, localName));
			}
		}
		return str.toString();
	}

	private String getPrefixFor(final QName qname) {
		String prefix = null;
		final Collection<String> prefixes = getNamespacePrefixResolver().getPrefixes(qname.getNamespaceURI());
		if (!prefixes.isEmpty()) {
			prefix = prefixes.iterator().next();
		}
		return prefix;
	}
}
