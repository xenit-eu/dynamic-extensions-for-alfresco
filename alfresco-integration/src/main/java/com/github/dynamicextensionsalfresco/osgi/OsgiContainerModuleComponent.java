package com.github.dynamicextensionsalfresco.osgi;

import org.alfresco.repo.module.AbstractModuleComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.transaction.TransactionService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

/**
 * Module component that manages the OSGI child {@link ApplicationContext} and initializes the {@link FrameworkManager}
 * within it.
 * 
 * @author Laurens Fridael
 * 
 */
public class OsgiContainerModuleComponent extends AbstractModuleComponent implements ApplicationContextAware,
		FrameworkService {

	/* Dependencies */

	/**
	 * The containing {@link ApplicationContext}.
	 */
	private ConfigurableWebApplicationContext applicationContext;

	/* Configuration */

	private String[] applicationContextConfigLocations;

	/* State */

	/**
	 * The child {@link ApplicationContext} containing the OSGi framework.
	 * <p>
	 * This {@link ApplicationContext} can be destroyed and recreated, effectively enabling clients to restart the OSGi
	 * container.
	 */
	private ConfigurableWebApplicationContext childApplicationContext;

	private TransactionService transactionService;

	/* Main module operations */

	/**
	 * Initializes this module.
	 */
	@Override
	protected void executeInternal() {
		startFramework();
	}

	/**
	 * Called on bean destroy.
	 */
	public void destroy() {
		stopFramework();
	}

	/* Main FrameworkService operations */

	@Override
	public void restartFramework() {
		final RetryingTransactionHelper transactionHelper = getTransactionService().getRetryingTransactionHelper();
		transactionHelper.doInTransaction(new RetryingTransactionCallback<Void>() {

			@Override
			public Void execute() throws Throwable {
				return AuthenticationUtil.runAs(new RunAsWork<Void>() {

					@Override
					public Void doWork() throws Exception {
						try {
							stopFramework();
						} finally {
							startFramework();
						}
						return null;
					}
				}, AuthenticationUtil.SYSTEM_USER_NAME);
			}
		});
	}

	/* Utility operations */

	protected void startFramework() {
		if (childApplicationContext == null) {
			try {
				initializeOsgiContainerApplicationContext();
			} catch (final Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	private void initializeOsgiContainerApplicationContext() {
		childApplicationContext = new XmlWebApplicationContext();
		childApplicationContext.setParent(getApplicationContext());
		childApplicationContext.setServletContext(getApplicationContext().getServletContext());
		childApplicationContext.setConfigLocation(StringUtils.arrayToDelimitedString(
				getApplicationContextConfigLocations(), ","));
		childApplicationContext.refresh();
	}

	protected void stopFramework() {
		if (childApplicationContext != null) {
			try {
				childApplicationContext.close();
			} finally {
				childApplicationContext = null;
			}
		}
	}

	/* Dependencies */

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) {
		this.applicationContext = (ConfigurableWebApplicationContext) applicationContext;
	}

	protected ConfigurableWebApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public void setTransactionService(final TransactionService transactionService) {
		this.transactionService = transactionService;
	}

	protected TransactionService getTransactionService() {
		return transactionService;
	}

	/* Configuration */

	public void setApplicationContextConfigLocations(final String[] applicationContextConfigLocations) {
		this.applicationContextConfigLocations = applicationContextConfigLocations;
	}

	protected String[] getApplicationContextConfigLocations() {
		return applicationContextConfigLocations;
	}

	/* State */

	/**
	 * Obtains the {@link FrameworkManager} from the OSGi container{@link ApplicationContext}.
	 */
	public FrameworkManager getFrameworkManager() {
		Assert.state(childApplicationContext != null);
		return childApplicationContext.getBean(BeanNames.CONTAINER_FRAMEWORK_MANAGER, FrameworkManager.class);
	}

}
