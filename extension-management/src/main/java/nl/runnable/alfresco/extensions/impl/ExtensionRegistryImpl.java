package nl.runnable.alfresco.extensions.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.runnable.alfresco.extensions.Container;
import nl.runnable.alfresco.extensions.Extension;
import nl.runnable.alfresco.extensions.ExtensionRegistry;

public class ExtensionRegistryImpl implements ExtensionRegistry {

	private final Container container = new Container();

	private final List<Extension> extensions = new ArrayList<Extension>();

	private final Set<Long> coreBundleIds = new HashSet<Long>();

	@Override
	public Container getContainer() {
		return container;
	}

	@Override
	public void registerExtension(final Extension extension) {
		if (extensions.contains(extension) == false) {
			extension.setExtensionRegistry(this);
			extensions.add(extension);
		}
	}

	@Override
	public void unregisterExtension(final Extension extension) {
		if (extensions.contains(extension)) {
			extensions.remove(extension);
			extension.setExtensionRegistry(null);
		}
	}

	@Override
	public List<Extension> getExtensions() {
		return extensions;
	}

	@Override
	public void registerCoreBundle(final long bundleId) {
		coreBundleIds.add(bundleId);
	}

	@Override
	public boolean isCoreBundle(final long bundleId) {
		return coreBundleIds.contains(bundleId);
	}

}
