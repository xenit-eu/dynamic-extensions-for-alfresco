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

import nl.runnable.alfresco.repository.query.impl.LuceneQuerySerializer;

/**
 * Defines a strategy for serializing {@link Query} instances to their query language format.
 * 
 * @author Laurens Fridael
 * @see LuceneQuerySerializer
 */
public interface QuerySerializer {

	/**
	 * Obtains the query language that this serializer generates.
	 * 
	 * @return
	 */
	public String getLanguage();

	/**
	 * Serializes a given {@link Query} to String format.
	 * 
	 * @param query
	 * @return
	 * @throws IllegalArgumentException
	 *             If luceneQuery is null.
	 */
	public String serializeAsString(Query query);

	/**
	 * Serializes a given {@link Query} to String format using a given {@link VariableResolver} to fill in any
	 * variables.
	 * 
	 * @param query
	 * @param variableResolver
	 *            May be null, in which case the call is equivalent to {@link #serializeAsString(Query)}.
	 * @return
	 * @throws IllegalArgumentException
	 *             If query is null.
	 */
	public String serializeAsString(Query query, VariableResolver variableResolver);

}
