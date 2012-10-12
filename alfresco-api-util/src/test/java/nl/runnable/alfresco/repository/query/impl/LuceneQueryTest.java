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

import static nl.runnable.alfresco.repository.query.Variable.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import nl.runnable.alfresco.repository.mock.MockNamespacePrefixResolver;
import nl.runnable.alfresco.repository.node.impl.PathHelperImpl;
import nl.runnable.alfresco.repository.node.impl.SimplePathEncoder;
import nl.runnable.alfresco.repository.query.Field;
import nl.runnable.alfresco.repository.query.QueryBuilder;
import nl.runnable.alfresco.repository.query.QueryBuilderFactory;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.Path;
import org.alfresco.service.namespace.QName;
import org.junit.Before;
import org.junit.Test;

/**
 * Integration test for using {@link QueryBuilder} in combination with a {@link LuceneQuerySerializer}.
 * 
 * @author Laurens Fridael
 * 
 */
public class LuceneQueryTest {

	private static enum ExampleEnum {
		EXAMPLE;
	}

	private QueryBuilder query;

	private LuceneQuerySerializer querySerializer;

	private QueryBuilderFactory queryBuilderFactory;

	@Before
	public void setup() {
		queryBuilderFactory = new QueryBuilderFactoryImpl();

		query = queryBuilderFactory.createQueryBuilder();

		querySerializer = new LuceneQuerySerializer();

		final MockNamespacePrefixResolver namespacePrefixResolver = MockNamespacePrefixResolver
				.createWithCommonNamespaces();
		querySerializer.setNamespacePrefixResolver(namespacePrefixResolver);

		final NodeService nodeService = mock(NodeService.class);
		final Path path = new Path();
		final NodeRef root = new NodeRef("workspace", "SpacesStore", "1");
		final NodeRef companyHome = new NodeRef("workspace", "SpacesStore", "2");
		path.append(new Path.ChildAssocElement(new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, root, QName
				.createQName("app:companyhome", namespacePrefixResolver), companyHome, true, 0)));
		when(nodeService.getPath(any(NodeRef.class))).thenReturn(path);
		querySerializer.setNodeService(nodeService);

		final PathHelperImpl pathHelper = new PathHelperImpl();
		pathHelper.setNamespacePrefixResolver(namespacePrefixResolver);
		pathHelper.setPathEncoder(new SimplePathEncoder());
		querySerializer.setPathHelper(pathHelper);
	}

	@Test
	public void testPropertyMatchesString() {
		query.property(ContentModel.PROP_NAME).matches("test");
		assertEquals("@cm\\:name:\"test\"", serialized(query));
	}

	@Test
	public void testPropertyMatchesEnum() {
		query.property(ContentModel.PROP_NAME).matches(ExampleEnum.EXAMPLE);
		assertEquals("@cm\\:name:\"EXAMPLE\"", serialized(query));
	}

	@Test
	public void testPropertyStartsWith() {
		query.property(ContentModel.PROP_NAME).startsWith("test");
		assertEquals("@cm\\:name:\"test*\"", serialized(query));
	}

	@Test
	public void testPropertyContains() {
		query.property(ContentModel.PROP_NAME).contains("test");
		assertEquals("@cm\\:name:\"*test*\"", serialized(query));
	}

	/*
	 * @Test public void testPropertyMatchesDate() { query.property(ContentModel.PROP_MODIFIED).matches(createDate());
	 * assertEquals("@cm\\:modified:\"1970-01-01T01:02:03.000Z\"", serialized(query)); }
	 */

	@Test
	public void testPropertyMatchesNodeRef() {
		query.property(ContentModel.PROP_CATEGORIES).matches(new NodeRef("workspace", "SpacesStore", "12345"));
		assertEquals("@cm\\:categories:\"workspace://SpacesStore/12345\"", serialized(query));
	}

	@Test
	public void testPropertyIsBoolean() {
		query.property(ContentModel.PROP_INITIAL_VERSION).matches(true);
		assertEquals("@cm\\:initialVersion:true", serialized(query));
	}

	@Test
	public void testFieldMatchesString() {
		query.field(Field.PATH).matches("/app:companyhome");
		assertEquals("PATH:\"/app:companyhome\"", serialized(query));
	}

	@Test
	public void testFieldStringMatchesString() {
		query.field("customfield").matches("customvalue");
		assertEquals("customfield:\"customvalue\"", serialized(query));
	}

	@SuppressWarnings("serial")
	@Test
	public void testPropertyMatchesVariable() {
		query.property(ContentModel.PROP_NAME).matches(variable("value"));
		assertEquals("@cm\\:name:\"document.pdf\"", serialized(query, new HashMap<String, Object>() {
			{
				put("value", "document.pdf");
			}
		}));
	}

	@SuppressWarnings("serial")
	@Test
	public void testPropertyStartsWithVariable() {
		query.property(ContentModel.PROP_NAME).startsWith(variable("value"));
		assertEquals("@cm\\:name:\"document*\"", serialized(query, new HashMap<String, Object>() {
			{
				put("value", "document");
			}
		}));
	}

	@SuppressWarnings("serial")
	@Test
	public void testPropertyContainsVariable() {
		query.property(ContentModel.PROP_NAME).contains(variable("value"));
		assertEquals("@cm\\:name:\"*document*\"", serialized(query, new HashMap<String, Object>() {
			{
				put("value", "document");
			}
		}));
	}

	@Test
	public void testPropertyBetweenIntegerRange() {
		query.property(ContentModel.PROP_HITS).between(null, 10);
		assertEquals("@cm\\:hits:[MIN TO 10]", serialized(query));
	}

	@Test
	public void testPropertBetweenDateRange() {
		// TODO: Provide test case.
	}

	@Test
	public void testIsChildOf() {
		query.isChildOf(new NodeRef("workspace", "SpacesStore", "12345"));
		assertEquals("PATH:\"/app:companyhome//*\"", serialized(query));
	}

	@Test
	public void testIsImmediateChildOf() {
		query.isImmediateChildOf(new NodeRef("workspace", "SpacesStore", "12345"));
		assertEquals("PATH:\"/app:companyhome/*\"", serialized(query));
	}

	@Test
	public void testSubqueriesWithNestedQueryBuilders() {
		// First subquery.
		final QueryBuilder auditableAndClassifiable = queryBuilderFactory.createQueryBuilder();
		auditableAndClassifiable.hasAllAspectsOf(ContentModel.ASPECT_AUDITABLE, ContentModel.ASPECT_CLASSIFIABLE);
		// Second subquery.
		final QueryBuilder achivedContentOrFolders = queryBuilderFactory.createQueryBuilder();
		achivedContentOrFolders.isOfAnyTypeOf(ContentModel.TYPE_CONTENT, ContentModel.TYPE_FOLDER).and()
				.hasAspect(ContentModel.ASPECT_AUDITABLE);
		// Main query.
		query.subquery(auditableAndClassifiable).or().subquery(achivedContentOrFolders);
		assertEquals(
				"(ASPECT:cm\\:auditable AND ASPECT:cm\\:classifiable) OR ((TYPE:cm\\:content OR TYPE:cm\\:folder) AND ASPECT:cm\\:auditable)",
				serialized(query));
	}

	@SuppressWarnings("serial")
	@Test
	public void testSubqueries() {
		query.startSubquery().hasAllAspectsOf(ContentModel.ASPECT_AUDITABLE, ContentModel.ASPECT_CLASSIFIABLE)
				.endSubquery().or().startSubquery().isOfType(variable("type")).and()
				.hasAspect(ContentModel.ASPECT_AUDITABLE).endSubquery();
		assertEquals(
				"(ASPECT:cm\\:auditable AND ASPECT:cm\\:classifiable) OR (TYPE:cm\\:content AND ASPECT:cm\\:auditable)",
				serialized(query, new HashMap<String, Object>() {
					{
						put("type", ContentModel.TYPE_CONTENT);
					}
				}));
	}

	@Test
	public void testIsOfType() {
		query.isOfType(ContentModel.TYPE_CONTENT);
		assertEquals("TYPE:cm\\:content", serialized(query));
	}

	@SuppressWarnings("serial")
	@Test
	public void testIsOfTypeVariable() {
		query.isOfType(variable("type"));
		assertEquals("TYPE:cm\\:content", serialized(query, new HashMap<String, Object>() {
			{
				put("type", ContentModel.TYPE_CONTENT);
			}
		}));
	}

	@Test
	public void testIsOfAnyTypeOf() {
		query.isOfAnyTypeOf(ContentModel.TYPE_CONTENT, ContentModel.TYPE_PERSON);
		assertEquals("(TYPE:cm\\:content OR TYPE:cm\\:person)", serialized(query));
	}

	@Test
	public void testHasAspect() {
		query.hasAspect(ContentModel.ASPECT_CLASSIFIABLE);
		assertEquals("ASPECT:cm\\:classifiable", serialized(query));
	}

	@Test
	public void testHasAnyAspectOf() {
		query.hasAnyAspectOf(ContentModel.ASPECT_AUDITABLE, ContentModel.ASPECT_COPIEDFROM);
		assertEquals("ASPECT:cm\\:auditable OR ASPECT:cm\\:copiedfrom", serialized(query));
	}

	@Test
	public void testHasAllAspectsOf() {
		query.hasAllAspectsOf(ContentModel.ASPECT_COUNTABLE, ContentModel.ASPECT_LOCKABLE);
		assertEquals("ASPECT:cm\\:countable AND ASPECT:cm\\:lockable", serialized(query));
	}

	@Test
	public void testInvertedConditionsWithPrecedingNot() {
		query.property(ContentModel.PROP_NAME).not().matches("test").and().property(ContentModel.PROP_AUTHOR)
				.matches("john");
		assertEquals("NOT @cm\\:name:\"test\" AND @cm\\:author:\"john\"", serialized(query));
	}

	@Test
	public void testInvertedConditionsWithSucceedingNot() {
		query.property(ContentModel.PROP_NAME).matches("test").and().not().property(ContentModel.PROP_AUTHOR)
				.matches("john");
		assertEquals("@cm\\:name:\"test\" AND NOT @cm\\:author:\"john\"", serialized(query));
	}

	@Test
	public void testInvertedSubQuery() {
		query.not().startSubquery().property(ContentModel.PROP_NAME).matches("document.pdf").endSubquery();
		assertEquals("NOT (@cm\\:name:\"document.pdf\")", serialized(query));
	}

	@SuppressWarnings("serial")
	@Test
	public void testUnresolvedVariableHandling() {
		query.hasAspect(variable("aspect")).and().isOfType(ContentModel.TYPE_CONTENT).and()
				.property(ContentModel.PROP_NAME).matches(variable("name"));
		// No variables.
		assertEquals("TYPE:cm\\:content", serialized(query));
		// Only the first variable.
		assertEquals("ASPECT:cm\\:auditable AND TYPE:cm\\:content", serialized(query, new HashMap<String, Object>() {
			{
				put("aspect", ContentModel.ASPECT_AUDITABLE);
			}
		}));
		// Only the second variable
		assertEquals("TYPE:cm\\:content AND @cm\\:name:\"document.pdf\"",
				serialized(query, new HashMap<String, Object>() {
					{
						put("name", "document.pdf");
					}
				}));
		// Both variables.
		assertEquals("ASPECT:cm\\:auditable AND TYPE:cm\\:content AND @cm\\:name:\"document.pdf\"",
				serialized(query, new HashMap<String, Object>() {
					{
						put("aspect", ContentModel.ASPECT_AUDITABLE);
						put("name", "document.pdf");
					}
				}));
	}

	// State tests.

	/**
	 * Tests the handling of logical operations being added at the start of query. This should yield an
	 * IllegalStateException.
	 */
	@Test(expected = IllegalStateException.class)
	public void testLeadingLogicalOperationHandling() {
		// Next line yields an error, no conditions specified yet.
		query.and();
	}

	/**
	 * Tests the handling of missing logical operations. These should yield an IllegalStateException.
	 */
	@Test(expected = IllegalStateException.class)
	public void testMissingLogicalOperationHandling() {
		query.isOfType(ContentModel.TYPE_CONTENT);
		// Next line yields and error, because there is no logical operation between the conditions.
		query.hasAnyAspectOf(ContentModel.ASPECT_ARCHIVED);
	}

	/**
	 * Tests the handling of specifying multiple logical conditions. These should yield an IllegalStateException.
	 */
	@Test(expected = IllegalStateException.class)
	public void testMultipleLogicalOperationHandling() {
		query.isOfType(ContentModel.TYPE_CONTENT);
		// Next line yields an error because of multiple logical operations.
		query.and().or();
	}

	/**
	 * Tests the handling of trailing logical operations. These do not have any effect while additional conditions are
	 * not yet specified.
	 */
	@Test
	public void testTrailingLogicalOperationHandling() {
		query.isOfType(ContentModel.TYPE_CONTENT).and();
		// The trailing and() has no effect on the query being generated....
		assertEquals("TYPE:cm\\:content", serialized(query));
		// ...it's generated only when there is another condition being added.
		query.property(ContentModel.PROP_NAME).matches("my-document.pdf");
		assertEquals("TYPE:cm\\:content AND @cm\\:name:\"my-document.pdf\"", serialized(query));
	}

	/**
	 * Tests the handling of mismatched calls to {@link QueryBuilder#endSubquery()}.
	 */
	@Test(expected = IllegalStateException.class)
	public void testMismatchedEndSubqueryCalls() {
		// Correct.
		query.startSubquery().endSubquery();
		// Error, mismatched call.
		query.endSubquery();
	}

	// Utility methods

	private String serialized(final QueryBuilder query) {
		return querySerializer.serializeAsString(query.createQuery());
	}

	private String serialized(final QueryBuilder query, final Map<String, Object> variables) {
		return querySerializer.serializeAsString(query.createQuery(), new MapVariableResolver(variables));
	}

	/*
	 * private Date createDate() { final Calendar cal = Calendar.getInstance(); cal.setTime(new Date(0)); // 1-jan-1970
	 * cal.set(Calendar.HOUR_OF_DAY, 1); cal.set(Calendar.MINUTE, 2); cal.set(Calendar.SECOND, 3); return cal.getTime();
	 * }
	 */
}
