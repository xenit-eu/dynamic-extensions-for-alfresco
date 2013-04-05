package nl.runnable.alfresco.osgi;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.service.namespace.NamespaceException;
import org.alfresco.service.namespace.NamespaceService;
import org.springframework.util.Assert;

public class MockNamespaceService implements NamespaceService {

	/* State */

	private Map<String, String> namespacesByPrefix = new HashMap<String, String>();

	/* Main operations */

	@Override
	public String getNamespaceURI(final String prefix) throws NamespaceException {
		return namespacesByPrefix.get(prefix);
	}

	@Override
	public Collection<String> getPrefixes(final String namespaceURI) throws NamespaceException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<String> getPrefixes() {
		return namespacesByPrefix.keySet();
	}

	@Override
	public Collection<String> getURIs() {
		return namespacesByPrefix.values();
	}

	@Override
	public void registerNamespace(final String prefix, final String uri) {
		namespacesByPrefix.put(prefix, uri);
	}

	@Override
	public void unregisterNamespace(final String prefix) {
		namespacesByPrefix.remove(prefix);
	}

	/* State */

	public void setNamespacesByPrefix(final Map<String, String> namespacesByPrefix) {
		Assert.notNull(namespacesByPrefix);
		this.namespacesByPrefix = namespacesByPrefix;
	}
}
