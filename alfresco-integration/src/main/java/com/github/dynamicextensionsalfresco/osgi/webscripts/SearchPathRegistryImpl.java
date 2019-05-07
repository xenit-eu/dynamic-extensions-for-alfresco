package com.github.dynamicextensionsalfresco.osgi.webscripts;

import java.lang.reflect.Field;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.extensions.webscripts.SearchPath;
import org.springframework.extensions.webscripts.Store;
import org.springframework.util.Assert;

/**
 * {@link SearchPathRegistry} implementation that manages the {@link Store}s for a {@link SearchPath} instance.
 * <p>
 * Note: this implementation has to resort to reflection to manipulate the {@link Store} collection.
 * 
 * @author Laurens Fridael
 * 
 */
public class SearchPathRegistryImpl implements SearchPathRegistry {

	private SearchPath searchPath;

	@Required
	public void setSearchPath(final SearchPath searchPath) {
		Assert.notNull(searchPath);
		this.searchPath = searchPath;
	}

	@Override
	public void addStore(final Store store) {
		final Collection<Store> stores = getStores();
		if (stores.contains(store) == false) {
			stores.add(store);
		}
	}

	@Override
	public void removeStore(final Store store) {
		final Collection<Store> stores = getStores();
		if (stores.contains(store)) {
			stores.remove(store);
		}
	}

	@SuppressWarnings("unchecked")
	protected Collection<Store> getStores() {
		/*
		 * We have to resort to reflection to modify the SearchPath since there is no proper accessor method available
		 * to get a hold of the existing Stores. Note that SearchPath.getStores() only returns Stores that actually
		 * exist at making the method call, so we can't use that.
		 */
		try {
			/* Confusingly, the property holding the collection of Stores in the SearchPath is also called "searchPath". */
			final Field searchPathField = SearchPath.class.getDeclaredField("searchPath");
			searchPathField.setAccessible(true);
			return (Collection<Store>) searchPathField.get(searchPath);
		} catch (final SecurityException e) {
			throw new RuntimeException(e);
		} catch (final NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (final IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

}
