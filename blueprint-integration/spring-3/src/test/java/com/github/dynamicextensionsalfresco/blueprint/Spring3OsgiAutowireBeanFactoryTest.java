package com.github.dynamicextensionsalfresco.blueprint;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Integration test for {@link Spring3OsgiAutowireBeanFactory}.
 * 
 * @author Laurens Fridael
 * 
 */
public class Spring3OsgiAutowireBeanFactoryTest {

	/* Dependencies */

	private Example example;

	/* Main operations */

	@Before
	public void setup() {
		final ClassPathXmlApplicationContext parentApplicationContext = new ClassPathXmlApplicationContext(
				"OsgiAutowireBeanFactoryTest-parent-context.xml", getClass());
		final Spring3OsgiAutowireApplicationContext applicationContext = new Spring3OsgiAutowireApplicationContext(
				"OsgiAutowireBeanFactoryTest-child-context.xml", parentApplicationContext);
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
		assertNotNull(example.categoryService);
	}
}
