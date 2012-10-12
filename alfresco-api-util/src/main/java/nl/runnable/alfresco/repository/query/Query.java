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

package nl.runnable.alfresco.repository.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.alfresco.service.cmr.repository.StoreRef;
import org.springframework.util.Assert;

/**
 * A value object representing a Query. A query is a sequence of conditions and logical operations (AND, OR).
 * 
 * @author Laurens Fridael
 * @see QueryBuilder
 */
public class Query implements Iterable<Object>, Invertable {

	/* package */final List<Object> terms = new ArrayList<Object>();

	private final List<Sort<?>> sorts = new ArrayList<Sort<?>>();

	private int page = 0;

	private int pageSize = 0;

	private final StoreRef storeRef;

	private boolean inverted;

	/**
	 * Creates an instance for the default store.
	 */
	public Query() {
		this(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
	}

	/**
	 * Creates an instance for the given store.
	 * 
	 * @param storeRef
	 *            The StoreRef.
	 */
	public Query(final StoreRef storeRef) {
		Assert.notNull(storeRef, "StoreRef cannot be null.");
		this.storeRef = storeRef;
	}

	/**
	 * Creates an instance by copying another instance's terms, but using a possibly different StoreRef.
	 * 
	 * @param query
	 * @param
	 */
	public Query(final Query query, final StoreRef storeRef) {
		Assert.notNull(query, "Query cannot be null.");
		Assert.notNull(storeRef, "StoreRef cannot be null.");
		this.terms.addAll(query.terms);
		this.sorts.addAll(query.sorts);
		this.storeRef = storeRef;
	}

	/**
	 * Copy constructor for creating an instance from another instance.
	 * 
	 * @param query
	 *            The instance to create from.
	 */
	public Query(final Query query) {
		this(query, query.storeRef);
	}

	public StoreRef getStoreRef() {
		return storeRef;
	}

	public void addTerm(final Object term) {
		Assert.notNull(term, "Term cannot be null.");
		terms.add(term);
	}

	public void addSort(final Sort<?> sort) {
		Assert.notNull(sort, "Sort cannot be null.");
		sorts.add(sort);
	}

	public List<Sort<?>> getSorts() {
		return Collections.unmodifiableList(sorts);
	}

	public void setPage(final int page) {
		this.page = page;
	}

	public int getPage() {
		return page;
	}

	public void setPageSize(final int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageSize() {
		return pageSize;
	}

	public boolean isEmpty() {
		return terms.isEmpty();
	}

	@Override
	public Iterator<Object> iterator() {
		return terms.iterator();
	}

	@Override
	public void setInverted(final boolean inverted) {
		this.inverted = inverted;
	}

	@Override
	public boolean isInverted() {
		return inverted;
	}

}
