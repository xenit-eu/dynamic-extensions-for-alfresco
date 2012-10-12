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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import nl.runnable.alfresco.repository.node.PathHelper;
import nl.runnable.alfresco.repository.query.Field;
import nl.runnable.alfresco.repository.query.Invertable;
import nl.runnable.alfresco.repository.query.Query;
import nl.runnable.alfresco.repository.query.QueryBuilder;
import nl.runnable.alfresco.repository.query.QuerySerializer;
import nl.runnable.alfresco.repository.query.Variable;
import nl.runnable.alfresco.repository.query.VariableResolver;

import org.alfresco.repo.search.impl.querymodel.impl.lucene.LuceneQuery;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.Path;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

/**
 * {@link LuceneQuery}-based {@link QuerySerializer} implementation.
 * 
 * @author Laurens Fridael
 * 
 */
public class LuceneQuerySerializer implements QuerySerializer {

	private NamespacePrefixResolver namespacePrefixResolver;

	private NodeService nodeService;

	private PathHelper pathHelper;

	private final Map<QName, String> formattedQNamesByQName = new ConcurrentHashMap<QName, String>();

	@Required
	public void setNamespacePrefixResolver(final NamespacePrefixResolver namespacePrefixResolver) {
		Assert.notNull(namespacePrefixResolver, "NamespacePrefixResolver cannot be null.");
		this.namespacePrefixResolver = namespacePrefixResolver;
	}

	@Required
	public void setNodeService(final NodeService nodeService) {
		Assert.notNull(nodeService, "NodeService cannot be null.");
		this.nodeService = nodeService;
	}

	@Required
	public void setPathHelper(final PathHelper pathHelper) {
		Assert.notNull(pathHelper, "PathHelper cannot be null.");
		this.pathHelper = pathHelper;
	}

	@Override
	public String getLanguage() {
		return "lucene";
	}

	@Override
	public String serializeAsString(final Query query) {
		Assert.notNull(query, "Query cannot be null.");
		return doSerializeAsString(query, null);
	}

	@Override
	public String serializeAsString(final Query query, final VariableResolver variableResolver) {
		Assert.notNull(query, "Query cannot be null.");
		return doSerializeAsString(query, variableResolver);
	}

	protected String format(final QName qname) {
		final String formattedQName;
		if (formattedQNamesByQName.containsKey(qname)) {
			formattedQName = formattedQNamesByQName.get(qname);
		} else {
			formattedQName = format(qname.toPrefixString(namespacePrefixResolver));
			formattedQNamesByQName.put(qname, formattedQName);
		}
		return formattedQName;
	}

	private String doSerializeAsString(final Query query, final VariableResolver variableResolver) {
		final List<Object> terms = resolveVariables(query, variableResolver);
		final StringBuilder str = new StringBuilder();
		serializeTerms(str, terms, variableResolver);
		return str.toString();
	}

	private List<Object> resolveVariables(final Query query, final VariableResolver variableResolver) {
		final List<Object> terms = new ArrayList<Object>();
		for (final Iterator<Object> it = query.iterator(); it.hasNext();) {
			terms.add(it.next());
		}
		for (int i = 0; i < terms.size(); i++) {
			final Object term = terms.get(i);
			if (term instanceof Condition<?, ?>) {
				Condition<?, ?> condition = (Condition<?, ?>) term;
				final Object rhs = condition.getRhs();
				if (rhs instanceof Variable) {
					condition = resolveVariable(variableResolver, condition, (Variable) rhs);
					if (condition != null) {
						// Replace the condition with the Variable's resolved value.
						terms.set(i, condition);
					} else {
						// Variable could not be resolved.
						if (i == 0 && terms.size() > 1) {
							// We're at the start of the list of terms
							// Remove the following logical operation and the condition.
							terms.remove(i + 1);
							terms.remove(i);
						} else if (i > 1) {
							// We're in the middle or the end of the list of terms.
							// Remove the condition and any preceding logical operation.
							terms.remove(i);
							terms.remove(i - 1);
						}
						i--;
					}
				}
			}
		}
		return terms;
	}

	private void serializeTerms(final StringBuilder str, final List<Object> terms,
			final VariableResolver variableResolver) {
		for (final Iterator<Object> it = terms.iterator(); it.hasNext();) {
			final Object term = it.next();
			assert (term != null) : "Encountered null value in query term.";
			if (term instanceof Invertable && ((Invertable) term).isInverted()) {
				str.append("NOT ");
			}
			if (term instanceof LogicalOperation) {
				if (it.hasNext()) {
					// Add a logical operation only if it's NOT the last term.
					addLogicalOperation(str, (LogicalOperation) term);
				} else {
					// If the logical condition is the last term, remove any trailing whitespace.
					if (str.length() > 0) {
						str.delete(str.length() - 1, str.length());
					}
				}
			} else if (term instanceof Condition<?, ?>) {
				serializeCondition((Condition<?, ?>) term, str);
			} else if (term instanceof Query) {
				serializeSubquery((Query) term, variableResolver, str);
			} else if (term instanceof QueryBuilder) {
				serializeSubquery(((QueryBuilder) term).createQuery(), variableResolver, str);
			}
			if (it.hasNext()) {
				str.append(' ');
			}
		}
	}

	private Condition<?, ?> resolveVariable(final VariableResolver variableResolver, final Condition<?, ?> condition,
			final Variable variable) {
		if (variableResolver == null) {
			return null;
			/*
			 * throw new IllegalStateException(String.format(
			 * "Do not have a VariableResolver for resolving the variable: %s", variable.getName()));
			 */
		}
		final Object rhs = variableResolver.resolveVariable(variable.getName());
		if (rhs == null) {
			return null;
		} else {
			return new Condition<Object, Object>(condition.getLhs(), condition.getOperator(), rhs);
		}
	}

	private void addLogicalOperation(final StringBuilder str, final LogicalOperation logicalOperation) {
		switch (logicalOperation) {
		case AND:
			str.append("AND");
			break;
		case OR:
			str.append("OR");
			break;
		}
	}

	private void serializeCondition(final Condition<?, ?> condition, final StringBuilder str) {
		// if (condition.isInverted()) {
		// str.append("NOT ");
		// }
		// Left-hand side
		final Object lhs = condition.getLhs();
		if (lhs instanceof QName) {
			// Property
			str.append('@');
			str.append(format((QName) lhs));
		} else if (lhs instanceof Field) {
			// Field
			str.append(format(((Field) lhs).getFieldName()));
		} else if (lhs instanceof String) {
			// Custom field.
			str.append(format((String) lhs));
		}

		str.append(':');

		// Right hand side.
		final Object rhs = condition.getRhs();
		if (rhs instanceof String) {
			switch (condition.getOperator()) {
			case MATCHES:
				str.append('\"').append((String) rhs).append('\"');
				break;
			case STARTS_WITH:
				str.append('\"').append((String) rhs).append("*\"");
				break;
			case CONTAINS:
				str.append("\"*").append((String) rhs).append("*\"");
				break;
			}
		} else if (rhs instanceof Enum) {
			str.append('\"').append(((Enum<?>) rhs).name()).append('\"');
		} else if (rhs instanceof QName) {
			str.append(format((QName) rhs));
		} else if (rhs instanceof Date) {
			str.append(format((Date) rhs));
		} else if (rhs instanceof NodeRef) {
			str.append('\"').append(rhs).append('\"');
		} else if (rhs instanceof NodePath) {
			assert (condition.getOperator() == Operator.STARTS_WITH);
			final NodePath nodePath = (NodePath) rhs;
			final Path path = nodeService.getPath(nodePath.getNodeRef());
			str.append('\"').append(pathHelper.convertPathToString(path));
			if (nodePath.isImmediateChildrenOnly()) {
				str.append("/*");
			} else {
				str.append("//*");
			}
			str.append('\"');
		} else if (rhs instanceof Boolean) {
			str.append(String.valueOf(rhs));
		} else if (rhs instanceof Range) {
			final Range<?> range = (Range<?>) rhs;
			str.append(String.format("[%s TO %s]", formatMin(range.getMin()), formatMax(range.getMax())));
		}
	}

	private void serializeSubquery(final Query subquery, final VariableResolver variableResolver,
			final StringBuilder str) {
		// Ignore empty subquery
		if (subquery.isEmpty() == false) {
			str.append('(');
			str.append(doSerializeAsString(subquery, variableResolver));
			str.append(')');
		}
	}

	// Internal utility methods for formatting values to Lucene format.

	private String formatMin(final Object value) {
		if (value != null) {
			return formatValue(value);
		} else {
			return "MIN";
		}
	}

	private String formatMax(final Object value) {
		if (value != null) {
			return formatValue(value);
		} else {
			return "MAX";
		}
	}

	private String formatValue(final Object value) {
		Assert.notNull(value, "Value cannot be null.");
		if (value instanceof String) {
			return format((String) value);
		} else if (value instanceof Date) {
			return format((Date) value);
		} else if (value instanceof QName) {
			return format((QName) value);
		} else if (value instanceof Integer) {
			return format((Integer) value);
		} else {
			throw new IllegalArgumentException(String.format("Unsupported format: " + value.getClass()));
		}

	}

	private String format(final Integer value) {
		return String.valueOf(value);
	}

	private String format(final String value) {
		return value.replace(":", "\\:");
	}

	private String format(final Date date) {
		// DateFormat is NOT thread-safe, so create a local instance.
		return new SimpleDateFormat("'\"'yyyy-MM-dd'T'HH:mm:ss.SSS'Z\"'").format(date);
	}

}
