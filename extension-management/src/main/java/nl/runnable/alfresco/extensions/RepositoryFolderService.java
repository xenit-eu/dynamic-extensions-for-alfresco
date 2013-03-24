package nl.runnable.alfresco.extensions;

import java.io.InputStream;

import org.alfresco.service.cmr.repository.NodeRef;

/**
 * Defines operations for obtaining Dynamic Extensions-specific folders from the repository.
 * <p>
 * Implementations should create folders if they do not exist.
 * 
 * @author Laurens Fridael
 * 
 */
public interface RepositoryFolderService {

	/**
	 * Obtains the {@link NodeRef} to the Dynamic Extensions base folder.
	 * 
	 * @return
	 */
	NodeRef getBaseFolder();

	/**
	 * Obtains the {@link NodeRef} to the folder that holds OSGi bundle JARs.
	 * 
	 * @return
	 */
	NodeRef getBundleFolder();

	/**
	 * Obtains the {@link NodeRef} to the folder containing configuration files.
	 * 
	 * @return
	 */
	NodeRef getConfigurationFolder();

	/**
	 * Obtains the {@link NodeRef} to the folder with installation history.
	 * 
	 * @return
	 */
	NodeRef getInstallationHistoryFolder();

	void saveExtension(InputStream data);
}
