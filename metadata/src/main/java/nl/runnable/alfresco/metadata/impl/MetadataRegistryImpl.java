package nl.runnable.alfresco.metadata.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.runnable.alfresco.metadata.Metadata;
import nl.runnable.alfresco.metadata.MetadataRegistry;

public class MetadataRegistryImpl implements MetadataRegistry {

	private final List<Metadata> allMetadata = new ArrayList<Metadata>();

	private final Set<Long> coreBundleIds = new HashSet<Long>();

	@Override
	public void registerMetadata(final Metadata metadata) {
		if (allMetadata.contains(metadata) == false) {
			metadata.setMetadataRegistry(this);
			allMetadata.add(metadata);
		}
	}

	@Override
	public void unregisterMetadata(final Metadata metadata) {
		if (allMetadata.contains(metadata)) {
			allMetadata.remove(metadata);
			metadata.setMetadataRegistry(null);
		}
	}

	@Override
	public List<Metadata> getAllMetadata() {
		return allMetadata;
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
