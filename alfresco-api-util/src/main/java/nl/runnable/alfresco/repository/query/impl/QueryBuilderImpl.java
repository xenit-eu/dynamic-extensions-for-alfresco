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

import java.util.Date;

import nl.runnable.alfresco.repository.query.Field;
import nl.runnable.alfresco.repository.query.Invertable;
import nl.runnable.alfresco.repository.query.Query;
import nl.runnable.alfresco.repository.query.QueryBuilder;
import nl.runnable.alfresco.repository.query.Sort;
import nl.runnable.alfresco.repository.query.Variable;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.springframework.util.Assert;

/**
 * Default {@link QueryBuilder} implementation.
 * 
 * @author Laurens Fridael
 * 
 */
public class QueryBuilderImpl implements QueryBuilder, Invertable {

	private final QueryBuilder parent;

	/**
	 * The query being populated.
	 */
	private final Query query;

	/**
	 * The current logical operation.
	 */
	private LogicalOperation logicalOperation = null;

	/**
	 * The left-hand side of the current condition.
	 */
	private Object leftHandSide = null;

	private boolean invertNextCondition = false;

	private boolean inverted = false;

	protected QueryBuilderImpl(final Query query, final QueryBuilder parent) {
		Assert.notNull(query, "Query cannot be null.");
		this.query = query;
		this.parent = parent;
	}

	protected QueryBuilder getParent() {
		return parent;
	}

	protected Query getQuery() {
		return query;
	}

	protected void startCondition(final Object leftHandSide) {
		if (getQuery().isEmpty() == false) {
			Assert.state(logicalOperation != null,
					"No next logical operation available. Make sure you put logical operations between conditions.");
			getQuery().addTerm(logicalOperation);
		}
		logicalOperation = null;
		this.leftHandSide = leftHandSide;
	}

	protected void endCondition(final Operator operator, final Object rightHandSide) {
		Assert.state(leftHandSide != null, "No left-hand side available");
		final Condition<Object, Object> condition = new Condition<Object, Object>(leftHandSide, operator, rightHandSide);
		getQuery().addTerm(condition);
		condition.setInverted(invertNextCondition);
		invertNextCondition = false;
		logicalOperation = null;
	}

	protected void addCondition(final Object condition) {
		if (getQuery().isEmpty() == false) {
			Assert.state(logicalOperation != null,
					"No next logical operation available. Make sure you put logical operations between conditions.");
			getQuery().addTerm(logicalOperation);
		}
		getQuery().addTerm(condition);
		if (condition instanceof Invertable) {
			((Invertable) condition).setInverted(invertNextCondition);
		}
		invertNextCondition = false;
		logicalOperation = null;
	}

	@Override
	public Query createQuery() {
		return new Query(getQuery());
	}

	@Override
	public QueryBuilder property(final QName property) {
		Assert.notNull(property, "Property cannot be null.");
		startCondition(property);
		return this;
	}

	@Override
	public QueryBuilder field(final Field field) {
		Assert.notNull(field);
		startCondition(field);
		return this;
	}

	@Override
	public QueryBuilder field(final String field) {
		Assert.notNull(field);
		startCondition(field);
		return this;
	}

	@Override
	public QueryBuilder matches(final String value) {
		Assert.hasText(value, "Value cannot be empty.");
		endCondition(Operator.MATCHES, value);
		return this;
	}

	@Override
	public QueryBuilder matches(final Enum<?> value) {
		endCondition(Operator.MATCHES, value);
		return this;
	}

	@Override
	public QueryBuilder matches(final QName value) {
		Assert.notNull(value, "Value cannot be null.");
		endCondition(Operator.MATCHES, value);
		return this;
	}

	@Override
	public QueryBuilder matches(final NodeRef value) {
		Assert.notNull(value, "Value cannot be null");
		endCondition(Operator.MATCHES, value);
		return this;
	}

	@Override
	public QueryBuilder matches(final Date value) {
		Assert.notNull(value, "Value cannot be null.");
		endCondition(Operator.MATCHES, value);
		return this;
	}

	@Override
	public QueryBuilder matches(final boolean value) {
		endCondition(Operator.MATCHES, value);
		return this;
	}

	@Override
	public QueryBuilder matches(final Variable variable) {
		Assert.notNull(variable, "Variable cannot be null.");
		endCondition(Operator.MATCHES, variable);
		return this;
	}

	@Override
	public QueryBuilder startsWith(final String value) {
		Assert.hasText(value, "Value cannot be empty.");
		endCondition(Operator.STARTS_WITH, value);
		return this;
	}

	@Override
	public QueryBuilder startsWith(final Variable variable) {
		Assert.notNull(variable, "Variable cannot be null.");
		endCondition(Operator.STARTS_WITH, variable);
		return this;
	}

	@Override
	public QueryBuilder contains(final String value) {
		Assert.hasText(value, "Value cannot be empty.");
		endCondition(Operator.CONTAINS, value);
		return this;
	}

	@Override
	public QueryBuilder contains(final Variable variable) {
		Assert.notNull(variable, "Variable cannot be null.");
		endCondition(Operator.CONTAINS, variable);
		return this;
	}

	@Override
	public QueryBuilder between(final Integer min, final Integer max) {
		endCondition(Operator.MATCHES, new Range<Integer>(min, max));
		return this;
	}

	@Override
	public QueryBuilder between(final Date from, final Date to) {
		endCondition(Operator.MATCHES, new Range<Date>(from, to));
		return this;
	}

	@Override
	public QueryBuilder isChildOf(final NodeRef nodeRef) {
		Assert.notNull(nodeRef, "NodeRef cannot be null.");
		addCondition(new Condition<Field, NodePath>(Field.PATH, Operator.STARTS_WITH, new NodePath(nodeRef)));
		return this;
	}

	@Override
	public QueryBuilder isImmediateChildOf(final NodeRef nodeRef) {
		addCondition(new Condition<Field, NodePath>(Field.PATH, Operator.STARTS_WITH, new NodePath(nodeRef, true)));
		return this;
	}

	@Override
	public QueryBuilder isOfType(final QName type) {
		Assert.notNull(type, "Type cannot be null.");
		addCondition(new Condition<Field, QName>(Field.TYPE, Operator.MATCHES, type));
		return this;
	}

	@Override
	public QueryBuilder isOfType(final Variable variable) {
		Assert.notNull(variable, "Variable cannot be null.");
		addCondition(new Condition<Field, Variable>(Field.TYPE, Operator.MATCHES, variable));
		return this;
	}

	@Override
	public QueryBuilder isOfAnyTypeOf(final QName... types) {
		Assert.notEmpty(types, "Types cannot be empty.");
		final QueryBuilder subquery = startSubquery();
		for (int i = 0, n = types.length; i < n; i++) {
			final QName type = types[i];
			Assert.notNull(type, "Type cannot be null.");
			subquery.isOfType(type);
			if (i < n - 1) {
				subquery.or();
			}
		}
		subquery.endSubquery();
		return this;
	}

	@Override
	public QueryBuilder hasAspect(final QName aspect) {
		Assert.notNull(aspect, "Aspect cannot be null.");
		addCondition(new Condition<Field, QName>(Field.ASPECT, Operator.MATCHES, aspect));
		return this;
	}

	@Override
	public QueryBuilder hasAspect(final Variable variable) {
		Assert.notNull(variable, "Variable cannot be null.");
		addCondition(new Condition<Field, Variable>(Field.ASPECT, Operator.MATCHES, variable));
		return this;
	}

	@Override
	public QueryBuilder hasAnyAspectOf(final QName... aspects) {
		Assert.notEmpty(aspects, "Aspects cannot be empty.");
		for (int i = 0, n = aspects.length; i < n; i++) {
			final QName aspect = aspects[i];
			Assert.notNull(aspect, "Aspect cannot be null.");
			addCondition(new Condition<Field, QName>(Field.ASPECT, Operator.MATCHES, aspect));
			if (i < n - 1) {
				or();
			}
		}
		return this;
	}

	@Override
	public QueryBuilder hasAllAspectsOf(final QName... aspects) {
		Assert.notEmpty(aspects, "Aspects cannot be empty.");
		for (int i = 0, n = aspects.length; i < n; i++) {
			final QName aspect = aspects[i];
			Assert.notNull(aspect, "Aspect cannot be null.");
			addCondition(new Condition<Field, QName>(Field.ASPECT, Operator.MATCHES, aspect));
			if (i < n - 1) {
				and();
			}
		}
		return this;
	}

	@Override
	public void sortBy(final QName property, final boolean ascending) {
		query.addSort(new Sort<QName>(property, ascending));
	}

	@Override
	public void sortBy(final String field, final boolean ascending) {
		query.addSort(new Sort<String>(field, ascending));
	}

	@Override
	public void page(final int page) {
		query.setPage(page);
	}

	@Override
	public void pageSize(final int pageSize) {
		query.setPageSize(pageSize);
	}

	@Override
	public QueryBuilder and() {
		Assert.state(getQuery().isEmpty() == false, "No conditions specified yet, cannot use logical operation.");
		Assert.state(logicalOperation == null,
				"Logical operation is already specified. Make sure you specify only one operation between conditions.");
		logicalOperation = LogicalOperation.AND;
		return this;
	}

	@Override
	public QueryBuilder or() {
		Assert.state(getQuery().isEmpty() == false, "No conditions specified yet, cannot use logical operation.");
		Assert.state(logicalOperation == null,
				"Logical operation is already specified. Make sure you specify only one operation between conditions.");
		logicalOperation = LogicalOperation.OR;
		return this;
	}

	@Override
	public QueryBuilder not() {
		invertNextCondition = true;
		return this;
	}

	@Override
	public QueryBuilder startSubquery() {
		final QueryBuilderImpl subqueryBuilder = new QueryBuilderImpl(new Query(), this);
		addCondition(subqueryBuilder);
		return subqueryBuilder;
	};

	@Override
	public QueryBuilder endSubquery() {
		Assert.state(this.getParent() != null,
				"No parent query found. Make sure the numbers of calls to endSubquery() match that of calls to startSubquery().");
		return this.getParent();
	}

	@Override
	public QueryBuilder subquery(final Query subquery) {
		Assert.notNull(subquery, "Subquery cannot be null.");
		addCondition(subquery);
		return this;
	}

	@Override
	public QueryBuilder subquery(final QueryBuilder subquery) {
		Assert.notNull(subquery, "Subquery cannot be null.");
		addCondition(subquery);
		return this;
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
