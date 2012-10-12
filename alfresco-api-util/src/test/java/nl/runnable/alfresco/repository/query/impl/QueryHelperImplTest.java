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

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Collections;

import nl.runnable.alfresco.repository.mock.MockNamespacePrefixResolver;
import nl.runnable.alfresco.repository.query.QueryBuilder;
import nl.runnable.alfresco.repository.query.QueryBuilderFactory;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

/**
 * {@link QueryHelperImpl} unit test.
 * 
 * @author Laurens Fridael
 * 
 */
public class QueryHelperImplTest {

	private QueryHelperImpl queryHelper;

	private SearchService mockSearchService;

	private QueryBuilderFactory queryBuilderFactory;

	@Before
	public void setup() {
		queryHelper = new QueryHelperImpl();

		mockSearchService = mock(SearchService.class);
		final ResultSet mockResultSet = mock(ResultSet.class);
		when(mockSearchService.query(any(SearchParameters.class))).thenReturn(mockResultSet);
		when(mockResultSet.getNodeRefs()).thenReturn(Collections.<NodeRef> emptyList());
		queryHelper.setSearchService(mockSearchService);

		final LuceneQuerySerializer luceneQuerySerializer = new LuceneQuerySerializer();
		luceneQuerySerializer.setNamespacePrefixResolver(MockNamespacePrefixResolver.createWithCommonNamespaces());
		queryHelper.setQuerySerializer(luceneQuerySerializer);

		queryBuilderFactory = new QueryBuilderFactoryImpl();
	}

	/**
	 * Tests {@link QueryHelperImpl#query(nl.runnable.alfresco.repository.search.Query)}.
	 */
	@Test
	public void testQuery() {
		final QueryBuilder queryBuilder = queryBuilderFactory.createQueryBuilder();
		queryBuilder.isOfType(ContentModel.TYPE_CONTENT);
		queryHelper.query(queryBuilder.createQuery());

		final ArgumentCaptor<SearchParameters> searchParameters = ArgumentCaptor.forClass(SearchParameters.class);
		verify(mockSearchService).query(searchParameters.capture());
		assertEquals("lucene", searchParameters.getValue().getLanguage());
		assertEquals("TYPE:cm\\:content", searchParameters.getValue().getQuery());
	}

	/**
	 * Tests {@link QueryHelperImpl#queryNodeRefs(nl.runnable.alfresco.repository.search.Query)}.
	 */
	@Test
	public void testQueryNodeRefs() {
		final QueryBuilder queryBuilder = queryBuilderFactory.createQueryBuilder();
		queryBuilder.hasAspect(ContentModel.ASPECT_AUDITABLE);
		queryHelper.queryNodeRefs(queryBuilder.createQuery());

		final ArgumentCaptor<SearchParameters> searchParameters = ArgumentCaptor.forClass(SearchParameters.class);
		verify(mockSearchService).query(searchParameters.capture());
		assertEquals("lucene", searchParameters.getValue().getLanguage());
		assertEquals("ASPECT:cm\\:auditable", searchParameters.getValue().getQuery());
	}

}
