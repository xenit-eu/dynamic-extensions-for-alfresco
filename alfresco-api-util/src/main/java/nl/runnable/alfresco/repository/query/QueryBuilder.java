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

import java.util.Date;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 * Defines a fluent API for building {@link Query} objects, consisting of a sequence of conditions and logical
 * operations.
 * <p>
 * Conditions consist of a left-hand side expression, an operator and a right-hand side expression. An example
 * condition: <code>queryBuilder.property(ContentModel.PROP_NAME).matches("mydocument.pdf")</code>.
 * <p>
 * See the integration test LuceneQueryTest for examples on how to use this API.
 * 
 * @author Laurens Fridael
 * 
 */
public interface QueryBuilder {

	/**
	 * Starts a new condition with a given property QName as the left-hand expression.
	 * 
	 * @param property
	 *            The property QName
	 * @return This QueryBuilder
	 */
	public QueryBuilder property(QName property);

	/**
	 * Starts a new condition with a given {@link Field} as the left-hand expression.
	 * 
	 * @param field
	 *            The field
	 * @return This QueryBuilder
	 */
	public QueryBuilder field(Field field);

	/**
	 * Starts a new condition with a given field in String format as the left-hand expression. Mainly intended as an
	 * alternative to {@link #property(QName)} and {@link #field(Field)}.
	 * 
	 * @param field
	 *            The field name
	 * @return This QueryBuilder
	 */
	public QueryBuilder field(String field);

	/**
	 * Indicates the left-hand side of the condition should match with a given value.
	 * 
	 * @param value
	 *            the value
	 * @return This QueryBuilder
	 */
	public QueryBuilder matches(String value);

	/**
	 * Indicates the left-hand side of the condition should match with a given value.
	 * 
	 * @param value
	 *            the value
	 * @return This QueryBuilder
	 */
	public QueryBuilder matches(Enum<?> value);

	/**
	 * Indicates the left-hand side of the condition should match with a given Date value.
	 * 
	 * @param value
	 *            the value
	 * @return This QueryBuilder
	 */
	public QueryBuilder matches(Date value);

	/**
	 * Indicates the left-hand side of the condition should match with a given QName value.
	 * 
	 * @param value
	 *            the value
	 * @return This QueryBuilder
	 */
	public QueryBuilder matches(QName value);

	/**
	 * Indicates the left-hand side of the condition should match with a given NodeRef value.
	 * 
	 * @param value
	 *            the value
	 * @return This QueryBuilder
	 */
	public QueryBuilder matches(NodeRef value);

	/**
	 * Indicates the left-hand side of the condition should be equal to a given boolean value.
	 * 
	 * @param value
	 *            the value
	 * @return This QueryBuilder
	 */
	public QueryBuilder matches(boolean value);

	/**
	 * Indicates the left-hand side of the condition should match with the value provided by a given {@link Variable}.
	 * 
	 * @param variable
	 *            the variable
	 * @return This QueryBuilder
	 */
	public QueryBuilder matches(Variable variable);

	/**
	 * Indicates the left-hand side of the condition should start with a given value.
	 * 
	 * @param value
	 * @return This QueryBuilder
	 */
	public QueryBuilder startsWith(String value);

	/**
	 * Indicates the left-hand side of the condition should start with the value provided by a given {@link Variable}.
	 * 
	 * @param variable
	 *            the variable
	 * @return This QueryBuilder
	 */
	public QueryBuilder startsWith(Variable variable);

	/**
	 * Indicates the left-hand side of the condition should contains a given value.
	 * 
	 * @param value
	 *            the value
	 * @return This QueryBuilder
	 */
	public QueryBuilder contains(String value);

	/**
	 * Indicates the left-hand side of the condition should contain the value provided by a given {@link Variable}.
	 * 
	 * @param variable
	 *            the variable
	 * @return This QueryBuilder
	 */
	public QueryBuilder contains(Variable variable);

	/**
	 * Indicates the left-hand side of the condition should have a value between the given Integer range inclusive.
	 * 
	 * @param min
	 * @param max
	 * @return
	 */
	public QueryBuilder between(Integer min, Integer max);

	/**
	 * Indicates the left-hand side of the condition should have a value between the given date range inclusive.
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	public QueryBuilder between(Date from, Date to);

	/**
	 * Specifies a condition that selects nodes that are children of a given NodeRef.
	 * 
	 * @param nodeRef
	 *            the NodeRef
	 * @return This QueryBuilder
	 */
	public QueryBuilder isChildOf(NodeRef nodeRef);

	/**
	 * Specifies a condition that selects nodes that are children of a given NodeRef.
	 * 
	 * @param nodeRef
	 *            the NodeRef
	 * @return This QueryBuilder
	 */
	public QueryBuilder isImmediateChildOf(NodeRef nodeRef);

	/**
	 * Specifies a condition that selects nodes of a given type.
	 * 
	 * @param type
	 *            the type QName
	 * @return This QueryBuilder
	 */
	public QueryBuilder isOfType(QName type);

	/**
	 * Specifies a condition that selects nodes of the type provided by a given {@link Variable}.
	 * 
	 * @param variable
	 *            the variable
	 * @return This QueryBuilder
	 */
	public QueryBuilder isOfType(Variable variable);

	/**
	 * Specifies a condition that selects nodes of any of the provided types.
	 * 
	 * @param types
	 *            the type QNames
	 * @return This QueryBuilder
	 */
	public QueryBuilder isOfAnyTypeOf(QName... types);

	/**
	 * Specifies a condition that selects nodes of that have a given aspect.
	 * 
	 * @param aspect
	 *            the aspect QName
	 * @return This QueryBuilder
	 */
	public QueryBuilder hasAspect(QName aspect);

	/**
	 * Specifies a condition that selects nodes of that have the aspect provided by a given {@link Variable}.
	 * 
	 * @param variable
	 *            the variable
	 * @return This QueryBuilder
	 */
	public QueryBuilder hasAspect(Variable variable);

	/**
	 * Specifies a condition that selects nodes that have any of the given aspects.
	 * 
	 * @param aspects
	 *            the aspect QNames
	 * @return This QueryBuilder
	 */
	public QueryBuilder hasAnyAspectOf(QName... aspects);

	/**
	 * Specifies a condition that selects nodes that have all of the given aspects.
	 * 
	 * @param aspects
	 *            the aspect QNames
	 * @return This QueryBuilder
	 */
	public QueryBuilder hasAllAspectsOf(QName... aspects);

	// Sorting and paging

	public void sortBy(QName property, boolean ascending);

	public void sortBy(String field, boolean ascending);

	public void page(int page);

	public void pageSize(int pageSize);

	// Logical operations

	/**
	 * Specifies a logical AND operation between conditions.
	 * 
	 * @return This QueryBuilder
	 */
	public QueryBuilder and();

	/**
	 * Specifies a logical OR operation between conditions.
	 * 
	 * @return This QueryBuilder
	 */
	public QueryBuilder or();

	/**
	 * Specifies that the next condition should be inverted.
	 * 
	 * @return This QueryBuilder
	 */
	public QueryBuilder not();

	// Query creation.

	/**
	 * Creates the {@link Query} represented by this {@link QueryBuilder}.
	 * 
	 * @return This QueryBuilder
	 */
	public Query createQuery();

	/**
	 * Starts a subquery. Use {@link #endSubquery()} to pop back to the parent {@link QueryBuilder}.
	 * 
	 * @return the QueryBuilder representing the subquery
	 */
	public QueryBuilder startSubquery();

	/**
	 * Ends a subquery started by {@link #startSubquery()}.
	 * <p>
	 * Implementations should check for matching calls to {@link #startSubquery()} and throw an
	 * {@link IllegalArgumentException} if there is mismatch between number of calls.
	 * 
	 * @return the parent QueryBuilder
	 * @throws IllegalStateException
	 *             If there is no parent query.
	 */
	public QueryBuilder endSubquery();

	/**
	 * Specifies a subquery.
	 * 
	 * @param subquery
	 *            the subquery
	 * @return This QueryBuilder
	 */
	public QueryBuilder subquery(Query subquery);

	/**
	 * Short-hand for specifying a subquery when using multiple {@link QueryBuilder}s.
	 * 
	 * @param subquery
	 * @return This QueryBuilder
	 */
	public QueryBuilder subquery(QueryBuilder subquery);
}