package nl.runnable.alfresco.webscripts;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import nl.runnable.alfresco.webscripts.annotations.RequestParam;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.NamespaceService;
import org.junit.Test;
import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

/**
 * Integration test for {@link RequestParam} handling.
 * 
 * @author Laurens Fridael
 * 
 */
@ContextConfiguration(locations = "webscript-integration-test-context.xml", loader = SpringockitoContextLoader.class)
@DirtiesContext
public class RequestParamTest extends AbstractWebScriptAnnotationsTest {

	/* Dependencies */

	@ReplaceWithMock
	@Autowired
	private RequestParamHandler handler;

	@Autowired
	private NamespacePrefixResolver namespacePrefixResolver;

	/* Main operations */

	@Test
	public void testHandleNaming() {
		handleGetRequest(handler, "handleNaming",
				new MockWebScriptRequest().param("explicitlyNamed", "param1").param("implicitlyNamed", "param2"));
		verify(handler).handleNaming(eq("param1"), eq("param2"));
	}

	@Test
	public void testHandleDefaultValues() {
		handleGetRequest(handler, "handleDefaultValues", mock(WebScriptRequest.class));
		verify(handler).handleDefaultValues(eq("default"));
	}

	@Test
	public void testHandleArray() {
		handleGetRequest(handler, "handleArray", new MockWebScriptRequest().params("params", "hello", "world"));
		verify(handler).handleArray(eq(new String[] { "hello", "world" }));
	}

	@Test
	public void testDelimitedValues() {
		handleGetRequest(handler, "handleDelimitedValues", new MockWebScriptRequest().param("params", "hello,world"));
		verify(handler).handleDelimitedValues(eq(new String[] { "hello", "world" }));
	}

	@Test
	public void testHandleString() {
		handleGetRequest(handler, "handleString", new MockWebScriptRequest().param("param1", "string"));
		verify(handler).handleString(eq("string"), eq((String) null));

	}

	@Test
	public void testHandleInt() {
		handleGetRequest(handler, "handleInt", new MockWebScriptRequest().param("param1", "1").param("param2", "2"));
		verify(handler).handleInt(1, 2);

	}

	@Test
	public void testHandleLong() {
		handleGetRequest(handler, "handleLong", new MockWebScriptRequest().param("param1", "1").param("param2", "2"));
		verify(handler).handleLong(1l, 2l, 3l);
	}

	@Test
	public void testHandleBoolean() {
		handleGetRequest(handler, "handleBoolean", new MockWebScriptRequest().param("param1", "true"));
		verify(handler).handleBoolean(eq(true), eq((Boolean) null));
	}

	@Test
	public void handleQName() {
		when(namespacePrefixResolver.getNamespaceURI("cm")).thenReturn(NamespaceService.CONTENT_MODEL_1_0_URI);
		handleGetRequest(handler, "handleQName", new MockWebScriptRequest().param("qname", "cm:content"));
		verify(handler).handleQName(ContentModel.TYPE_CONTENT);
	}

	@Test
	public void handleNodeRef() {
		handleGetRequest(handler, "handleNodeRef", new MockWebScriptRequest().param("nodeRef",
				"workspace://SpacesStore/c269c803-4fd6-4aad-9114-3a42ff263fdc"));
		verify(handler).handleNodeRef(new NodeRef("workspace://SpacesStore/c269c803-4fd6-4aad-9114-3a42ff263fdc"));
	}
}
