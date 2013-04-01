package nl.runnable.alfresco.extensions;

import java.util.List;

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
	 * Obtains all {@link Extension}s.
	 * 
	 * @return
	 */
	public List<Extension> getExtensions();

}
