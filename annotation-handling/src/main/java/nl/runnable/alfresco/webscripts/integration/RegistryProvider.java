package nl.runnable.alfresco.webscripts.integration;

import java.util.Collection;

import org.springframework.extensions.webscripts.Registry;

/**
 * Strategy for obtaining Web Script {@link Registry} instances.
 * 
 * @author Laurens Fridael
 * 
 */
public interface RegistryProvider {

	public Collection<Registry> getRegistries();

}
