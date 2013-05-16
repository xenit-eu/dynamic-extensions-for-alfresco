package nl.runnable.alfresco.webscripts;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.StringWriter;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.extensions.webscripts.Container;
import org.springframework.extensions.webscripts.Runtime;
import org.springframework.extensions.webscripts.TemplateProcessor;
import org.springframework.extensions.webscripts.TemplateProcessorRegistry;
import org.springframework.extensions.webscripts.processor.FTLTemplateProcessor;
import org.springframework.util.ClassUtils;

/**
 * Tests the handling of response templates.
 * 
 * @author Laurens Fridael
 * 
 */
public class ResponseTemplateTest extends AbstractWebScriptAnnotationsTest {

	/* Dependencies */

	@Autowired
	private ResponseTemplateHandler handler;

	/* Main operations */

	@Test
	public void testHandleResponseTemplate() {
		final Runtime runtime = createRuntime();
		handleGet("/handleResponseTemplate", new MockWebScriptRequest().format("html").runtime(runtime),
				new MockWebScriptResponse().writer(new StringWriter()));
		verify(handler).handleResponseTemplate();
		final String templateName = templateNameForHandlerMethod("handleResponseTemplate");
		verify(getTemplateProcessor(runtime), atLeastOnce()).hasTemplate(templateName);
	}

	@Test
	public void testHandleResponseTemplateWithAnnotation() {
		final Runtime runtime = createRuntime();
		handleGet("/handleResponseTemplateWithAnnotation", new MockWebScriptRequest().format("html").runtime(runtime),
				new MockWebScriptResponse().writer(new StringWriter()));
		verify(handler).handleResponseTemplateWithAnnotation();
		final String templateName = templateNameForHandlerMethod("handleResponseTemplateWithAnnotation");
		verify(getTemplateProcessor(runtime), atLeastOnce()).hasTemplate(templateName);
	}

	@Test
	public void testHandleResponseTemplateWithCustomName() throws SecurityException, NoSuchMethodException {
		final Runtime runtime = createRuntime();
		handleGet("/handleResponseTemplateWithCustomName", new MockWebScriptRequest().format("html").runtime(runtime),
				new MockWebScriptResponse().writer(new StringWriter()));
		verify(getTemplateProcessor(runtime), atLeastOnce()).hasTemplate("custom-template.html");
	}

	/* Utility operations */

	protected Runtime createRuntime() {
		final FTLTemplateProcessor ftlTemplateProcessor = mock(FTLTemplateProcessor.class);
		when(ftlTemplateProcessor.hasTemplate(anyString())).thenReturn(true);
		final TemplateProcessorRegistry templateProcessorRegistry = new TemplateProcessorRegistry();
		templateProcessorRegistry.registerTemplateProcessor(ftlTemplateProcessor, "ftl", "Freemarker");
		final Container container = mock(Container.class);
		when(container.getTemplateProcessorRegistry()).thenReturn(templateProcessorRegistry);
		final Runtime runtime = mock(Runtime.class);
		when(runtime.getContainer()).thenReturn(container);
		return runtime;
	}

	protected String templateNameForHandlerMethod(final String methodName) {
		return String.format("%s.%s.get.html.200.ftl", ClassUtils.getQualifiedName(handler.getClass())
				.replace('.', '/'), methodName);
	}

	protected TemplateProcessor getTemplateProcessor(final Runtime runtime) {
		return runtime.getContainer().getTemplateProcessorRegistry().getTemplateProcessorByExtension("ftl");
	}

}
