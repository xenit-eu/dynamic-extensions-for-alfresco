package nl.runnable.alfresco.repository.query.impl;

import org.alfresco.service.descriptor.Descriptor;
import org.alfresco.service.descriptor.DescriptorService;
import org.alfresco.util.VersionNumber;
import org.alfresco.util.registry.NamedObjectRegistry;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * {@link FactoryBean} for creating a CannedQueryHelper. Only creates an instance when running on Alfresco 4.0 or
 * higher. This to ensure backwards-compatibility with Alfresco 3.4
 * 
 * @author Laurens Fridael
 * 
 */
public class CannedQueryHelperFactoryBean implements FactoryBean<Object>, ApplicationContextAware {

	private static final VersionNumber REQUIRED_ALFRESCO_VERSION = new VersionNumber("4.0");

	/* Dependencies */

	private DescriptorService descriptorService;

	private ApplicationContext applicationContext;

	/* State */

	private Object cannedQueryHelper;

	private boolean attemptedCreation = false;

	/* Main operations */

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public Class<?> getObjectType() {
		return null;
	}

	@Override
	public Object getObject() {
		if (attemptedCreation == false) {
			cannedQueryHelper = createCannedQueryHelper();
			attemptedCreation = true;
		}
		return cannedQueryHelper;
	}

	/* Utility operations */

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Object createCannedQueryHelper() {
		final Descriptor descriptor = getDescriptorService().getServerDescriptor();
		if (descriptor != null && descriptor.getVersionNumber().compareTo(REQUIRED_ALFRESCO_VERSION) >= 0) {
			final NamedObjectRegistry fileFolderCannedQueryRegistry = applicationContext.getBean(
					"fileFolderCannedQueryRegistry", NamedObjectRegistry.class);
			final CannedQueryHelperImpl cannedQueryHelper = new CannedQueryHelperImpl();
			cannedQueryHelper.setFileFolderCannedQueryFactoryRegistry(fileFolderCannedQueryRegistry);
			return cannedQueryHelper;
		} else {
			return null;
		}
	}

	/* Dependencies */

	public void setDescriptorService(final DescriptorService descriptorService) {
		this.descriptorService = descriptorService;
	}

	protected DescriptorService getDescriptorService() {
		return descriptorService;
	}

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

}
