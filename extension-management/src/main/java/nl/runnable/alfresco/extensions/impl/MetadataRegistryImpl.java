package nl.runnable.alfresco.extensions.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.runnable.alfresco.extensions.ContainerMetadata;
import nl.runnable.alfresco.extensions.ExtensionMetadata;
import nl.runnable.alfresco.extensions.MetadataRegistry;

public class MetadataRegistryImpl implements MetadataRegistry {

	private final ContainerMetadata containerMetadata = new ContainerMetadata();

	private final List<ExtensionMetadata> extensionsMetadata = new ArrayList<ExtensionMetadata>();

	private final Set<Long> coreBundleIds = new HashSet<Long>();

	@Override
	public ContainerMetadata getContainerMetadata() {
		return containerMetadata;
	}

	@Override
	public void registerExtension(final ExtensionMetadata metadata) {
		if (extensionsMetadata.contains(metadata) == false) {
			metadata.setMetadataRegistry(this);
			extensionsMetadata.add(metadata);
		}
	}

	@Override
	public void unregisterExtension(final ExtensionMetadata metadata) {
		if (extensionsMetadata.contains(metadata)) {
			extensionsMetadata.remove(metadata);
			metadata.setMetadataRegistry(null);
		}
	}

	@Override
	public List<ExtensionMetadata> getExtensionsMetadata() {
		return extensionsMetadata;
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
