package nl.runnable.alfresco.osgi.spring;

import org.alfresco.service.descriptor.DescriptorService;
import org.springframework.util.Assert;

public class AlfrescoLibraryVersionDetector extends AbstractLibraryVersionDetector {

	/* Dependencies */

	private DescriptorService descriptorService;

	/* Main operations */

	@Override
	protected String getBasePackageName() {
		return "org.alfresco";
	}

	@Override
	protected String doDetectLibraryVersion(final String packageName) {
		return getDescriptorService().getServerDescriptor().getVersionNumber().toString();
	}

	/* Dependencies */

	public void setDescriptorService(final DescriptorService descriptorService) {
		Assert.notNull(descriptorService);
		this.descriptorService = descriptorService;
	}

	protected DescriptorService getDescriptorService() {
		return descriptorService;
	}

}
