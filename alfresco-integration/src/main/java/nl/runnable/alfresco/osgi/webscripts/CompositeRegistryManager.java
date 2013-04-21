package nl.runnable.alfresco.osgi.webscripts;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.Registry;
import org.springframework.util.Assert;

/**
 * Manages the registration and unregistration of {@link Registry} instances with a {@link CompositeRegistry}.
 * 
 * @author Laurens Fridael
 * 
 */
public class CompositeRegistryManager {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	/* Dependencies */

	private CompositeRegistry compositeRegistry;

	/* Configuration */

	private List<Registry> registries = Collections.emptyList();

	/* Operations */

	public void registerRegistries() {
		Assert.state(getCompositeRegistry() != null);

		if (logger.isDebugEnabled()) {
			logger.debug("Registering {} registries with the CompositeRegistry.", registries.size());
		}
		for (final Registry registry : getRegistries()) {
			getCompositeRegistry().addRegistry(registry);
			registry.reset();
		}
	}

	public void unregisterRegistries() {
		Assert.state(getCompositeRegistry() != null);

		if (logger.isDebugEnabled()) {
			logger.debug("Unregistering {} registries with the CompositeRegistry.", registries.size());
		}
		for (final Registry registry : getRegistries()) {
			getCompositeRegistry().removeRegistry(registry);
		}
	}

	/* Dependencies */

	public void setCompositeRegistry(final CompositeRegistry compositeRegistry) {
		this.compositeRegistry = compositeRegistry;
	}

	protected CompositeRegistry getCompositeRegistry() {
		return compositeRegistry;
	}

	/* Configuration */

	public void setRegistries(final List<Registry> registries) {
		Assert.notNull(registries);
		this.registries = registries;
	}

	public List<Registry> getRegistries() {
		return registries;
	}
}
