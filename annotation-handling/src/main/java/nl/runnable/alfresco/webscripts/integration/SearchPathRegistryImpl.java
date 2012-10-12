/*
Copyright (c) 2012, Runnable
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
 * Neither the name of Runnable nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package nl.runnable.alfresco.webscripts.integration;

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
