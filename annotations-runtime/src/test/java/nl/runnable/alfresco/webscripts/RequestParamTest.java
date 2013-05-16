package nl.runnable.alfresco.webscripts;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import nl.runnable.alfresco.webscripts.annotations.RequestParam;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.NamespaceService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Integration test for {@link RequestParam} handling.
 * 
 * @author Laurens Fridael
 * 
 */
public class RequestParamTest extends AbstractWebScriptAnnotationsTest {

	/* Dependencies */

	@Autowired
	private RequestParamHandler handler;

	@Autowired
	private NamespacePrefixResolver namespacePrefixResolver;

	/* Main operations */

	@Test
	public void testHandleNaming() {
		handleGet("/handleNaming",
				new MockWebScriptRequest().param("explicitlyNamed", "param1").param("implicitlyNamed", "param2"));
		verify(handler).handleNaming(eq("param1"), eq("param2"));
	}

	@Test
	public void testHandleDefaultValues() {
		handleGet("/handleDefaultValues");
		verify(handler).handleDefaultValues(eq("default"));
	}

	@Test
	public void testHandleArray() {
		handleGet("/handleArray", new MockWebScriptRequest().params("params", "hello", "world"));
		verify(handler).handleArray(eq(new String[] { "hello", "world" }));
	}

	@Test
	public void testDelimitedValues() {
		handleGet("/handleDelimitedValues", new MockWebScriptRequest().param("params", "hello,world"));
		verify(handler).handleDelimitedValues(eq(new String[] { "hello", "world" }));
	}

	@Test
	public void testHandleString() {
		handleGet("/handleString", new MockWebScriptRequest().param("param1", "string"));
		verify(handler).handleString(eq("string"), eq((String) null));
	}

	@Test
	public void testHandleInt() {
		handleGet("/handleInt", new MockWebScriptRequest().param("param1", "1").param("param2", "2"));
		verify(handler).handleInt(1, 2);

	}

	@Test
	public void testHandleLong() {
		handleGet("/handleLong", new MockWebScriptRequest().param("param1", "1").param("param2", "2"));
		verify(handler).handleLong(1l, 2l, 3l);
	}

	@Test
	public void testHandleBoolean() {
		handleGet("/handleBoolean", new MockWebScriptRequest().param("param1", "true"));
		verify(handler).handleBoolean(eq(true), eq((Boolean) null));
	}

	@Test
	public void handleQName() {
		when(namespacePrefixResolver.getNamespaceURI("cm")).thenReturn(NamespaceService.CONTENT_MODEL_1_0_URI);
		handleGet("/handleQName", new MockWebScriptRequest().param("qname", "cm:content"));
		verify(handler).handleQName(ContentModel.TYPE_CONTENT);
	}

	@Test
	public void handleNodeRef() {
		handleGet("/handleNodeRef", new MockWebScriptRequest().param("nodeRef",
				"workspace://SpacesStore/c269c803-4fd6-4aad-9114-3a42ff263fdc"));
		verify(handler).handleNodeRef(new NodeRef("workspace://SpacesStore/c269c803-4fd6-4aad-9114-3a42ff263fdc"));
	}
}
