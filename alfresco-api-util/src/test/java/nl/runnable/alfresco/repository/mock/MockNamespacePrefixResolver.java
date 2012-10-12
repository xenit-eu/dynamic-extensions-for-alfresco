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

package nl.runnable.alfresco.repository.mock;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.springframework.util.Assert;

/**
 * Mock {@link NamespacePrefixResolver} implementation.
 * 
 * @author Laurens Fridael
 * 
 */
public class MockNamespacePrefixResolver implements NamespacePrefixResolver {

	/**
	 * Creates a new instance populated with some core namespaces used by default by Alfresco repository.
	 * 
	 * @return The new instance.
	 */
	public static MockNamespacePrefixResolver createWithCommonNamespaces() {
		final HashMap<String, String> namespaceURIsByPrefix = new HashMap<String, String>();
		namespaceURIsByPrefix.put("app", "http://www.alfresco.org/model/application/1.0");
		namespaceURIsByPrefix.put("cm", "http://www.alfresco.org/model/content/1.0");
		namespaceURIsByPrefix.put("d", "http://www.alfresco.org/model/dictionary/1.0");
		namespaceURIsByPrefix.put("sys", "http://www.alfresco.org/model/system/1.0");
		return new MockNamespacePrefixResolver(namespaceURIsByPrefix);
	}

	private Map<String, String> namespaceURIsByPrefix;

	/**
	 * Construct an blank instance.
	 */
	public MockNamespacePrefixResolver() {
		namespaceURIsByPrefix = new HashMap<String, String>();
	}

	/**
	 * Constructs an instance with a given map as configuration.
	 * 
	 * @param namespaceURIsByPrefix
	 */
	public MockNamespacePrefixResolver(final Map<String, String> namespaceURIsByPrefix) {
		Assert.notNull(namespaceURIsByPrefix, "Map cannot be null.");
		this.namespaceURIsByPrefix = namespaceURIsByPrefix;
	}

	public void setNamespaceURIsByPrefix(final Map<String, String> namespaceURIsByPrefix) {
		Assert.notNull(namespaceURIsByPrefix, "Map cannot be null.");
		this.namespaceURIsByPrefix = namespaceURIsByPrefix;
	}

	@Override
	public String getNamespaceURI(final String prefix) {
		return namespaceURIsByPrefix.get(prefix);
	}

	@Override
	public Collection<String> getPrefixes(final String namespaceURI) {
		for (final Entry<String, String> entry : namespaceURIsByPrefix.entrySet()) {
			if (entry.getValue().equals(namespaceURI)) {
				return Arrays.asList(entry.getKey());
			}
		}
		return null;
	}

	@Override
	public Collection<String> getPrefixes() {
		return namespaceURIsByPrefix.keySet();
	}

	@Override
	public Collection<String> getURIs() {
		return namespaceURIsByPrefix.values();
	}

}
