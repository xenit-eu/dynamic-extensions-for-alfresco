package nl.runnable.alfresco.examples;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import nl.runnable.alfresco.annotations.AlfrescoPlatform;

import org.alfresco.repo.bulkimport.BulkFilesystemImporter;

/**
 * This bean will only be instantiated on Alfresco 4.0.
 * 
 * @author Laurens Fridael
 * 
 */
@ManagedBean
@AlfrescoPlatform(minVersion = "4.0")
public class Alfresco4SpecificBean {

	/**
	 * {@link BulkFilesystemImporter} is specific to Alfresco 4.0 API.
	 */
	@Inject
	private BulkFilesystemImporter bulkFilesystemImporter;

}
