package nl.runnable.alfresco.metadata.impl;

import java.util.ArrayList;
import java.util.List;

import nl.runnable.alfresco.metadata.Metadata;
import nl.runnable.alfresco.metadata.MetadataRegistry;

public class MetadataRegistryImpl implements MetadataRegistry {

	private final List<Metadata> allMetadata = new ArrayList<Metadata>();

	@Override
	public void registerMetadata(final Metadata metadata) {
		if (allMetadata.contains(metadata) == false) {
			allMetadata.add(metadata);
		}
	}

	@Override
	public void unregisterMetadata(final Metadata metadata) {
		allMetadata.remove(metadata);
	}

	public List<Metadata> getAllMetadata() {
		return allMetadata;
	}

}
