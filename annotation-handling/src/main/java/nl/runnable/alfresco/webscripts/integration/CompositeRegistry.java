package nl.runnable.alfresco.webscripts.integration;

import java.util.Collection;

import org.springframework.extensions.webscripts.Registry;

/**
 * {@link Registry} interface extension intended for compositions. Typical implementations delegate their operations in
 * a fixed order to each {@link Registry} in the composition, returning the first available result for operation that
 * return a single element or merging all available results in case the operation returns a {@link Collection}.
 * 
 * @author Laurens Fridael
 * 
 */
public interface CompositeRegistry extends Registry {

	/**
	 * Adds a given {@link Registry} to this composition.
	 * 
	 * @param registry
	 */
	public void addRegistry(Registry registry);

	/**
	 * Removes a given {@link Registry} from this composition.
	 * 
	 * @param registry
	 */
	public void removeRegistry(Registry registry);

}
