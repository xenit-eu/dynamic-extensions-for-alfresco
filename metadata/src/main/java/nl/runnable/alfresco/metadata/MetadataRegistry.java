package nl.runnable.alfresco.metadata;

import java.util.Collection;

/**
 * Provides a means for registering and looking up {@link ExtensionMetadata} across Dynamic Extensions.
 * 
 * @author Laurens Fridael
 * 
 */
public interface MetadataRegistry {

	/**
	 * Provides access to the {@link ContainerMetadata}.
	 * 
	 * @return
	 */
	public ContainerMetadata getContainerMetadata();

	/**
	 * Registers {@link ExtensionMetadata} during bundle installation.
	 * 
	 * @param metadata
	 */
	public void registerExtension(ExtensionMetadata metadata);

	/**
	 * Unregisters {@link ExtensionMetadata}. Should be called when a bundle is uninstalled.
	 * 
	 * @param metadata
	 */
	public void unregisterExtension(ExtensionMetadata metadata);

	/**
	 * Obtains all {@link ExtensionMetadata}.
	 * 
	 * @return
	 */
	public Collection<ExtensionMetadata> getExtensionsMetadata();

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
