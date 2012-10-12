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

import java.util.List;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;

/**
 * Defines operations for performing searches using {@link Query} objects.
 * 
 * @author Laurens Fridael
 * 
 */
public interface QueryHelper {

	/**
	 * Performs a query and returns its {@link ResultSet}. Clients are responsible for managing (and more importantly:
	 * closing) the ResultSet.
	 * 
	 * @param query
	 * @return The query's ResultSet.
	 */
	public ResultSet query(Query query);

	/**
	 * Performs a query and returns its {@link ResultSet}. Callers are responsible for managing (and more importantly:
	 * closing) the ResultSet.
	 * 
	 * @param query
	 * @param variableResolver
	 * @return The query's ResultSet.
	 */
	public ResultSet query(Query query, VariableResolver variableResolver);

	/**
	 * Performs a query and returns the results as a list of {@link NodeRef}s.
	 * <p>
	 * Implementations are responsible for closing the underlying ResultSet.
	 * 
	 * @param query
	 * @return The results as a List of NodeRefs.
	 */
	public List<NodeRef> queryNodeRefs(Query query);

	/**
	 * Performs a query and returns the results as a list of {@link NodeRef}s.
	 * <p>
	 * Implementations are responsible for closing the underlying ResultSet.
	 * 
	 * @param query
	 * @param variableResolver
	 * @return The results as a List of NodeRefs.
	 */
	public List<NodeRef> queryNodeRefs(Query query, VariableResolver variableResolver);

	/**
	 * Converts a given {@link Query} to {@link SearchParameters} for use with the Alfresco {@link SearchService} API.
	 * 
	 * @param query
	 * @return The SearchParameters
	 */
	public SearchParameters createSearchParameters(Query query);

	/**
	 * Converts a given {@link Query} to {@link SearchParameters} for use with the Alfresco {@link SearchService} API.
	 * 
	 * @param query
	 * @param variableResolver
	 * @return The SearchParameters
	 */
	public SearchParameters createSearchParameters(Query query, VariableResolver variableResolver);
}
