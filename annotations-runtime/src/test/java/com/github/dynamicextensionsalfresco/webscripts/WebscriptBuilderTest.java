package nl.runnable.alfresco.webscripts;

import nl.runnable.alfresco.webscripts.annotations.WebScript;
import org.junit.Test;
import org.mockito.internal.stubbing.answers.Returns;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
/**
 * Verify detection of duplicate webscript id's: duplicate @Uri methods in the same WebScript.
 *
 * @author Laurent Van der Linden
 */
public class WebscriptBuilderTest extends AbstractWebScriptAnnotationsTest {
	@Test
	public void testDuplicatId() {
		final String webscriptId = "webscriptId";
		AnnotationWebScriptBuilder builder = new AnnotationWebScriptBuilder();
		ConfigurableListableBeanFactory dummyBeanFactory = mock(ConfigurableListableBeanFactory.class);
		when(dummyBeanFactory.getType(webscriptId)).thenAnswer(new Returns(DuplicateIdWebScript.class));
		final DuplicateIdWebScript instance = new DuplicateIdWebScript();
		when(dummyBeanFactory.findAnnotationOnBean(webscriptId, WebScript.class)).thenReturn(instance.getClass().getAnnotation(WebScript.class));
		when(dummyBeanFactory.getBean(webscriptId)).thenReturn(instance);

		builder.setBeanFactory(dummyBeanFactory);
		try {
			builder.createWebScripts(webscriptId);
			fail("registering duplicate Uri methods is not allowed");
		} catch (IllegalStateException e) {
			assertTrue("expected duplicate id error", e.getMessage().contains("Duplicate Web Script ID"));
		} catch (Exception x) {
			fail("expected error was IllegalArgumentException, but was " + x.getClass());
		}
	}
}
