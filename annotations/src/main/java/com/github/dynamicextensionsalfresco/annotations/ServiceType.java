package com.github.dynamicextensionsalfresco.annotations;

/**
 * Represents the type of {@link AlfrescoService} dependency to use for autowiring.
 * <p>
 * Client code should typically use the default, high-level services. Low-level Alfresco services typically do not have
 * built-in support for transaction, auditing or security, but may be required for specialized use cases.
 * 
 * 
 * @author Laurens Fridael
 * 
 */
public enum ServiceType {
	/** Indicates a default high-level service. */
	DEFAULT,
	/**
	 * Indicates a low-level service.
	 */
	LOW_LEVEL
}
