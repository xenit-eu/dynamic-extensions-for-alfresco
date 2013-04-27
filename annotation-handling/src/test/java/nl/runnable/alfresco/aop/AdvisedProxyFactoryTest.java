package nl.runnable.alfresco.aop;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.LinkedHashSet;
import java.util.Set;

import nl.runnable.alfresco.aop.annotations.RunAs;
import nl.runnable.alfresco.aop.annotations.RunAsSystem;
import nl.runnable.alfresco.aop.annotations.Transactional;

import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.aopalliance.aop.Advice;
import org.junit.Before;
import org.junit.Test;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.Advised;

public class AdvisedProxyFactoryTest {

	/* Dependencies */

	private AdvisedProxyFactory transactionalProxyFactory;

	private RetryingTransactionHelper retryingTransactionHelper;

	private ExampleBean bean;

	/* Main operations */

	@Before
	public void setup() {
		retryingTransactionHelper = mock(RetryingTransactionHelper.class);
		final Set<AdviceResolver> adviceResolvers = new LinkedHashSet<AdviceResolver>();
		adviceResolvers.add(new TransactionalAdviceResolver(retryingTransactionHelper));
		adviceResolvers.add(new RunAsAdviceResolver());
		adviceResolvers.add(new RunAsSystemAdviceResolver());
		transactionalProxyFactory = new AdvisedProxyFactory(adviceResolvers);
		bean = transactionalProxyFactory.createAdvisedProxy(new ExampleBean());
	}

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

	@Test
	public void testHasRunAsAdvice() {
		assertTrue(hasAdvice((Advised) bean, RunAsAdvice.class));
	}

	@Test
	public void testHasRunAsSystemAdvice() {
		assertTrue(hasAdvice((Advised) bean, RunAsSystemAdvice.class));
	}

	protected boolean hasAdvice(final Advised advised, final Class<? extends Advice> adviceType) {
		for (final Advisor advisor : advised.getAdvisors()) {
			if (adviceType.isInstance(advisor.getAdvice())) {
				return true;
			}
		}
		return false;
	}

	/* Utility classes */

	static class ExampleBean {

		@Transactional
		@RunAs("admin")
		public void doWithDefaultSettings() {
		}

		@Transactional(readOnly = true)
		public void doWithReadOnly() {
		}

		@Transactional(readOnly = true, requiresNew = true)
		@RunAsSystem
		public void doWithReadOnlyAndRequiresNew() {
		}
	}

}
