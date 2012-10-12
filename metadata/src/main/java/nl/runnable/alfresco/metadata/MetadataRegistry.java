package nl.runnable.alfresco.metadata;

import java.util.Collection;

/**
 * Provides a means for registering and looking up {@link Metadata} across Dynamic Extensions.
 * 
 * @author Laurens Fridael
 * 
 */
public interface MetadataRegistry {

	public void registerMetadata(Metadata metadata);

	public void unregisterMetadata(Metadata metadata);

	public Collection<Metadata> getAllMetadata();
}
