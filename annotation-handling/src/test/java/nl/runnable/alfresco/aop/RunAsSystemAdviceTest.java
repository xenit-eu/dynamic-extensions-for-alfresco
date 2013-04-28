package nl.runnable.alfresco.aop;

import static org.junit.Assert.*;
import nl.runnable.alfresco.annotations.RunAsSystem;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Integration test for {@link RunAsSystemAdvice} applied to {@link RunAsSystem}-annotated methods.
 * 
 * @author Laurens Fridael
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("AdviceTest-context.xml")
public class RunAsSystemAdviceTest {

	@Autowired
	private ExampleBean bean;

	@Test
	public void testHasRunAsAdvice() {
		assertTrue(AdviceUtil.hasAdvice((Advised) bean, RunAsSystemAdvice.class));
	}
}
