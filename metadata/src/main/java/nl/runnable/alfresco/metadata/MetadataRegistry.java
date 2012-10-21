package nl.runnable.alfresco.metadata;

import java.util.Collection;

/**
 * Provides a means for registering and looking up {@link Metadata} across Dynamic Extensions.
 * 
 * @author Laurens Fridael
 * 
 */
public interface MetadataRegistry {

	/**
	 * Registers {@link Metadata} during bundle installation.
	 * 
	 * @param metadata
	 */
	public void registerMetadata(Metadata metadata);

	/**
	 * Unregisters {@link Metadata}. Should be called when a bundle is uninstalled.
	 * 
	 * @param metadata
	 */
	public void unregisterMetadata(Metadata metadata);

	/**
	 * Obtains all {@link Metadata}.
	 * 
	 * @return
	 */
	public Collection<Metadata> getAllMetadata();

	/**
	 * Registers a bundle ID as being a core bundle. These ID's can then be used to distinguish between bundles
	 * belonging to the framework itself.
	 * 
	 * @param bundleId
	 */
	public void registerCoreBundle(long bundleId);

	/**
	 * Tests if the given bundle ID represents a core bundle.
	 * 
	 * @param bundleId
	 * @return
	 */
	public boolean isCoreBundle(long bundleId);

}
