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

package nl.runnable.alfresco.repository.query.impl;

import java.util.List;

import nl.runnable.alfresco.repository.query.Query;
import nl.runnable.alfresco.repository.query.QueryHelper;
import nl.runnable.alfresco.repository.query.QuerySerializer;
import nl.runnable.alfresco.repository.query.Sort;
import nl.runnable.alfresco.repository.query.VariableResolver;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

/**
 * Default {@link QueryHelper} implementation.
 * 
 * @author Laurens Fridael
 * 
 */
public class QueryHelperImpl implements QueryHelper {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private SearchService searchService;

	private QuerySerializer querySerializer;

	@Required
	public void setSearchService(final SearchService searchService) {
		Assert.notNull(searchService, "SearchService cannot be null.");
		this.searchService = searchService;
	}

	protected SearchService getSearchService() {
		return searchService;
	}

	@Required
	public void setQuerySerializer(final QuerySerializer querySerializer) {
		Assert.notNull(querySerializer, "QuerySerializer cannot be null.");
		this.querySerializer = querySerializer;
	}

	protected QuerySerializer getQuerySerializer() {
		return querySerializer;
	}

	@Override
	public ResultSet query(final Query query) {
		Assert.notNull(query, "Query cannot be null.");
		return query(query, null);
	}

	@Override
	public ResultSet query(final Query query, final VariableResolver variableResolver) {
		Assert.notNull(query, "Query cannot be null.");
		final SearchParameters searchParameters = createSearchParameters(query, variableResolver);
		if (logger.isDebugEnabled()) {
			logger.debug("Performing {} query: {}",
					new Object[] { searchParameters.getLanguage(), searchParameters.getQuery() });
		}
		return getSearchService().query(searchParameters);
	}

	@Override
	public List<NodeRef> queryNodeRefs(final Query query) {
		Assert.notNull(query, "Query cannot be null.");
		return queryNodeRefs(query, null);
	}

	@Override
	public List<NodeRef> queryNodeRefs(final Query query, final VariableResolver variableResolver) {
		Assert.notNull(query, "Query cannot be null.");
		final SearchParameters searchParameters = createSearchParameters(query, variableResolver);
		if (logger.isDebugEnabled()) {
			logger.debug("Performing {} query: {}",
					new Object[] { searchParameters.getLanguage(), searchParameters.getQuery() });
		}
		final ResultSet resultSet = getSearchService().query(searchParameters);
		try {
			return resultSet.getNodeRefs();
		} finally {
			resultSet.close();
		}
	}

	@Override
	public SearchParameters createSearchParameters(final Query query) {
		return createSearchParameters(query, null);
	}

	@Override
	public SearchParameters createSearchParameters(final Query query, final VariableResolver variableResolver) {
		Assert.notNull(query);
		final SearchParameters searchParameters = new SearchParameters();
		searchParameters.addStore(query.getStoreRef());
		searchParameters.setLanguage(getQuerySerializer().getLanguage());
		searchParameters.setQuery(getQuerySerializer().serializeAsString(query, variableResolver));
		for (final Sort<?> sort : query.getSorts()) {
			if (sort.getField() instanceof QName) {
				searchParameters.addSort(String.format("@%s", sort.getField()), sort.isAscending());
			} else if (sort.getField() != null) {
				searchParameters.addSort(sort.getField().toString(), sort.isAscending());
			}
		}
		if (query.getPage() > 0 && query.getPageSize() > 0) {
			searchParameters.setSkipCount(query.getPage() * query.getPageSize());
			searchParameters.setMaxItems(query.getPageSize());
		}
		return searchParameters;
	}

}
