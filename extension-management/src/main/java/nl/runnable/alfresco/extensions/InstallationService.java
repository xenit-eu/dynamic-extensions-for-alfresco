package nl.runnable.alfresco.extensions;

/**
 * Defines operations for registering {@link Installation}s.
 * 
 * @author Laurens Fridael
 * 
 */
public interface InstallationService {

	/**
	 * Registers the installation of the given {@link Extension}. This operation creates a {@link Installation} entry if
	 * none exist yet.
	 * 
	 * @param extension
	 */
	void registerInstallation(Extension extension);

	/**
	 * Obtains the {@link Installation} for the {@link Extension} with teh given name.
	 * 
	 * @param extensionName
	 * @return The matching {@link Installation} or null if none could be found.
	 */
	Installation getInstallation(String extensionName);

	/**
	 * Clears the {@link Installation} for {@link Extension} with the given name.
	 * 
	 * @param extension
	 */
	void clearInstallation(String extensionName);

}
