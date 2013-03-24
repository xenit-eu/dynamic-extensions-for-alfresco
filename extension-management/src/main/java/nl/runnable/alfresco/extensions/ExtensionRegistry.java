package nl.runnable.alfresco.extensions;

import java.util.Collection;

/**
 * Provides a means for registering and looking up {@link Extension} across Dynamic Extensions.
 * 
 * @author Laurens Fridael
 * 
 */
public interface ExtensionRegistry {

	/**
	 * Provides access to the {@link Container}.
	 * 
	 * @return
	 */
	public Container getContainer();

	/**
	 * Registers {@link Extension} during bundle installation.
	 * 
	 * @param extension
	 */
	public void registerExtension(Extension extension);

	/**
	 * Unregisters {@link Extension}. Should be called when a bundle is uninstalled.
	 * 
	 * @param extension
	 */
	public void unregisterExtension(Extension extension);

	/**
	 * Obtains all {@link Extension}.
	 * 
	 * @return
	 */
	public Collection<Extension> getExtensions();

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
