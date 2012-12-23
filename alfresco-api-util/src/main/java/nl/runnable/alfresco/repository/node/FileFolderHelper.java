package nl.runnable.alfresco.repository.node;

import org.alfresco.repo.model.filefolder.HiddenAspect;

/**
 * Defines operations for working with files and folders.
 * 
 * @author Laurens Fridael
 * 
 */
public interface FileFolderHelper {
	/**
	 * Provides access to the {@link HiddenAspect} service.
	 */
	public HiddenAspect getHiddenAspect();
}
