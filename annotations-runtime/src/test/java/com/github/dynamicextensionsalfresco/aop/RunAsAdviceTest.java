package com.github.dynamicextensionsalfresco.aop;

import com.github.dynamicextensionsalfresco.annotations.RunAs;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertTrue;

/**
 * Integration test for {@link RunAsAdvice} applied to {@link RunAs}-annotated methods.
 * 
 * @author Laurens Fridael
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("AdviceTest-context.xml")
public class RunAsAdviceTest {

	@Autowired
	private ExampleBean bean;

	@Test
	public void testHasRunAsAdvice() {
		assertTrue(AdviceUtil.hasAdvice((Advised) bean, RunAsAdvice.class));
	}
}
