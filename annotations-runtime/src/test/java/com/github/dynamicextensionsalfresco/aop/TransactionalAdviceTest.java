package com.github.dynamicextensionsalfresco.aop;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import com.github.dynamicextensionsalfresco.annotations.Transactional;

import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Integration test for {@link TransactionalAdvice} applied to {@link Transactional}-annotated methods.
 * 
 * @author Laurens Fridael
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("AdviceTest-context.xml")
public class TransactionalAdviceTest {

	@Autowired
	private RetryingTransactionHelper retryingTransactionHelper;

	@Autowired
	private ExampleBean bean;

	@Test
	@SuppressWarnings("unchecked")
	public void testWithDefaultSettings() {
		bean.doWithDefaultSettings();
		verify(retryingTransactionHelper).doInTransaction(any(RetryingTransactionCallback.class), eq(false), eq(false));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testWithReadOnly() {
		bean.doWithReadOnly();
		verify(retryingTransactionHelper).doInTransaction(any(RetryingTransactionCallback.class), eq(true), eq(false));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testWithReadOnlyAndRequiresNew() {
		bean.doWithReadOnlyAndRequiresNew();
		verify(retryingTransactionHelper).doInTransaction(any(RetryingTransactionCallback.class), eq(true), eq(true));
	}
}
