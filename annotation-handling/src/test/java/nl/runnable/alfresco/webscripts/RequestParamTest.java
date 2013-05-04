package nl.runnable.alfresco.webscripts;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import nl.runnable.alfresco.webscripts.annotations.RequestParam;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.NamespaceService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Integration test for {@link RequestParam} handling.
 * 
 * @author Laurens Fridael
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = SpringockitoContextLoader.class)
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
		final WebScriptRequest request = new MockWebScriptRequestBuilder().param("explicitlyNamed", "param1")
				.param("implicitlyNamed", "param2").build();
		handleGetRequest(handler, "handleNaming", request, mock(WebScriptResponse.class));
		verify(handler).handleNaming(eq("param1"), eq("param2"));
	}

	@Test
	public void testHandleDefaultValues() {
		handleGetRequest(handler, "handleDefaultValues", mock(WebScriptRequest.class), mock(WebScriptResponse.class));
		verify(handler).handleDefaultValues(eq("default"));
	}

	@Test
	public void testHandleArray() {
		final WebScriptRequest request = new MockWebScriptRequestBuilder().params("params", "hello", "world").build();
		handleGetRequest(handler, "handleArray", request, mock(WebScriptResponse.class));
		verify(handler).handleArray(eq(new String[] { "hello", "world" }));
	}

	@Test
	public void testDelimitedValues() {
		final WebScriptRequest request = new MockWebScriptRequestBuilder().param("params", "hello,world").build();
		handleGetRequest(handler, "handleDelimitedValues", request, mock(WebScriptResponse.class));
		verify(handler).handleDelimitedValues(eq(new String[] { "hello", "world" }));
	}

	@Test
	public void testHandleString() {
		final WebScriptRequest request = new MockWebScriptRequestBuilder().param("param1", "string").build();
		handleGetRequest(handler, "handleString", request, mock(WebScriptResponse.class));
		verify(handler).handleString(eq("string"), eq((String) null));

	}

	@Test
	public void testHandleInt() {
		final WebScriptRequest request = new MockWebScriptRequestBuilder().param("param1", "1").param("param2", "2")
				.build();
		handleGetRequest(handler, "handleInt", request, mock(WebScriptResponse.class));
		verify(handler).handleInt(1, 2);

	}

	@Test
	public void testHandleLong() {
		final WebScriptRequest request = new MockWebScriptRequestBuilder().param("param1", "1").param("param2", "2")
				.build();
		handleGetRequest(handler, "handleLong", request, mock(WebScriptResponse.class));
		verify(handler).handleLong(1l, 2l);
	}

	@Test
	public void testHandleBoolean() {
		final WebScriptRequest request = new MockWebScriptRequestBuilder().param("param1", "true").build();
		handleGetRequest(handler, "handleBoolean", request, mock(WebScriptResponse.class));
		verify(handler).handleBoolean(eq(true), eq((Boolean) null));
	}

	@Test
	public void handleQName() {
		when(namespacePrefixResolver.getNamespaceURI("cm")).thenReturn(NamespaceService.CONTENT_MODEL_1_0_URI);
		final WebScriptRequest request = new MockWebScriptRequestBuilder().param("qname", "cm:content").build();
		handleGetRequest(handler, "handleQName", request, mock(WebScriptResponse.class));
		verify(handler).handleQName(ContentModel.TYPE_CONTENT);
	}

	@Test
	public void handleNodeRef() {
		final WebScriptRequest request = new MockWebScriptRequestBuilder().param("nodeRef",
				"workspace://SpacesStore/c269c803-4fd6-4aad-9114-3a42ff263fdc").build();
		handleGetRequest(handler, "handleNodeRef", request, mock(WebScriptResponse.class));
		verify(handler).handleNodeRef(new NodeRef("workspace://SpacesStore/c269c803-4fd6-4aad-9114-3a42ff263fdc"));
	}
}
