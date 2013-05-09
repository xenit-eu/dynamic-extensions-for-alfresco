package nl.runnable.alfresco.osgi.spring;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Integration test for {@link AutowireBeanFactory}.
 * 
 * @author Laurens Fridael
 * 
 */
public class AutowireBeanFactoryTest {

	/* Dependencies */

	private Example example;

	/* Main operations */

	@Before
	public void setup() {
		final ClassPathXmlApplicationContext parentApplicationContext = new ClassPathXmlApplicationContext(
				"AutowireBeanFactoryTest-parent-context.xml", getClass());
		final AutowireApplicationContext applicationContext = new AutowireApplicationContext(
				"AutowireBeanFactoryTest-child-context.xml", parentApplicationContext);
		example = applicationContext.getBean(Example.class);
	}

	@Test
	public void testDefaultServiceAutowiring() {
		assertNotNull(example.nodeService);
		assertNotNull(example.namedNodeService);
		assertSame(example.nodeService, example.namedNodeService);
	}

	@Test
	public void testLowLevelServiceAutowiring() {
		assertNotNull(example.lowLevelNodeService);
		assertNotSame(example.nodeService, example.lowLevelNodeService);
	}

	@Test
	public void testTypeBasedAutowiring() {
		assertNotNull(example.namespaceService);
	}

	@Test
	public void testLowLevelServiceAutowiringFallback() {
		assertNotNull(example.lowLevelNodeService);
	}
}
