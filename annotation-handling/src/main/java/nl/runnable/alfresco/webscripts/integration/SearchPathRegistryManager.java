package nl.runnable.alfresco.webscripts.integration;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.Store;
import org.springframework.extensions.webscripts.TemplateProcessor;
import org.springframework.util.Assert;

/**
 * Manages the registration and unregistration of {@link Store}s in a {@link SearchPathRegistry}.
 * 
 * @author Laurens Fridael
 * 
 */
public class SearchPathRegistryManager {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	/* Dependencies */

	private SearchPathRegistry searchPathRegistry;
	private TemplateProcessor templateProcessor;

	/* Configuration */

	private List<Store> stores = Collections.emptyList();

	/* Operations */

	public void registerStores() {
		Assert.state(getSearchPathRegistry() != null);

		if (logger.isDebugEnabled()) {
			logger.debug("Registering {} stores with the SearchPathRegistry.", getStores().size());
		}
		for (final Store store : getStores()) {
			getSearchPathRegistry().addStore(store);
		}

		resetTemplateProcessor();
	}

	public void unregisterStores() {
		Assert.state(getSearchPathRegistry() != null);

		if (logger.isDebugEnabled()) {
			logger.debug("Unregistering {} stores with the SearchPathRegistry.", getStores().size());
		}
		for (final Store store : getStores()) {
			getSearchPathRegistry().removeStore(store);
		}
	}

	/**
	 * reset TemplateProcessor when new stores become available
	 */
	private void resetTemplateProcessor() {
		// need to change ContextClassLoader to UserTransaction aware ClassLoader
		final Thread currentThread = Thread.currentThread();
		final ClassLoader original = currentThread.getContextClassLoader();
		currentThread.setContextClassLoader(TemplateProcessor.class.getClassLoader());
		try {
			/*
			 * Workaround for issue with ConcurrentModificationException occurring during startup. See:
			 * https://github.com/lfridael/dynamic-extensions-for-alfresco/pull/8
			 */
			synchronized (templateProcessor) {
				templateProcessor.reset();
			}
		} finally {
			currentThread.setContextClassLoader(original);
		}
	}

	/* Dependencies */

	public void setSearchPathRegistry(final SearchPathRegistry searchPathRegistry) {
		this.searchPathRegistry = searchPathRegistry;
	}

	protected SearchPathRegistry getSearchPathRegistry() {
		return searchPathRegistry;
	}

	public void setTemplateProcessor(final TemplateProcessor templateProcessor) {
		this.templateProcessor = templateProcessor;
	}

	/* Configuration */

	public void setStores(final List<Store> stores) {
		Assert.notNull(stores);
		this.stores = stores;
	}

	public List<Store> getStores() {
		return stores;
	}
}
