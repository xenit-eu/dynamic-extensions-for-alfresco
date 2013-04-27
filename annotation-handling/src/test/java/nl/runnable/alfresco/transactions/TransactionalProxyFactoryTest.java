package nl.runnable.alfresco.transactions;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import nl.runnable.alfresco.transactions.annotations.Transactional;

import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.junit.Before;
import org.junit.Test;

public class TransactionalProxyFactoryTest {

	/* Dependencies */

	private TransactionalProxyFactory transactionalProxyFactory;

	private RetryingTransactionHelper retryingTransactionHelper;

	private TransactionalBean transactionalBean;

	/* Main operations */

	@Before
	public void setup() {
		retryingTransactionHelper = mock(RetryingTransactionHelper.class);
		transactionalProxyFactory = new TransactionalProxyFactory();
		transactionalProxyFactory.setRetryingTransactionHelper(retryingTransactionHelper);
		transactionalBean = transactionalProxyFactory.createTransactionalProxy(new TransactionalBean());
	}

	@Test
	public void testHasTransactionalMethods() {
		assertFalse(transactionalProxyFactory.hasTransactionalMethods(new Object()));
		assertTrue(transactionalProxyFactory.hasTransactionalMethods(transactionalBean));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testWithDefaultSettings() {
		transactionalBean.doWithDefaultSettings();
		verify(retryingTransactionHelper).doInTransaction(any(RetryingTransactionCallback.class), eq(false), eq(false));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testWithReadOnly() {
		transactionalBean.doWithReadOnly();
		verify(retryingTransactionHelper).doInTransaction(any(RetryingTransactionCallback.class), eq(true), eq(false));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testWithReadOnlyAndRequiresNew() {
		transactionalBean.doWithReadOnlyAndRequiresNew();
		verify(retryingTransactionHelper).doInTransaction(any(RetryingTransactionCallback.class), eq(true), eq(true));
	}

	/* Utility classes */

	static class TransactionalBean {

		@Transactional
		public void doWithDefaultSettings() {
		}

		@Transactional(readOnly = true)
		public void doWithReadOnly() {
		}

		@Transactional(readOnly = true, requiresNew = true)
		public void doWithReadOnlyAndRequiresNew() {
		}
	}

}
