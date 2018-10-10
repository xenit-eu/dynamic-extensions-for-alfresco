package com.github.dynamicextensionsalfresco.osgi;

import static org.junit.Assert.*;

import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.CategoryService;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.transaction.TransactionService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.launch.Framework;
import org.springframework.web.context.ConfigurableWebApplicationContext;

/**
 * Integration test for {@link OsgiContainerModuleComponent}.
 * <p>
 * Temporarily disabled until we find a solution to instantiating a mock {@link ConfigurableWebApplicationContext} from
 * an integration test.
 * 
 * @author Laurens Fridael
 * 
 */
// @ContextConfiguration
// @RunWith(SpringJUnit4ClassRunner.class)
public class OsgiContainerModuleComponentTest {

	private OsgiContainerModuleComponent moduleComponent;

	// @Autowired
	public void setModuleComponent(final OsgiContainerModuleComponent moduleComponent) {
		this.moduleComponent = moduleComponent;
	}

	// @Before
	public void setup() {
		moduleComponent.executeInternal();
	}

	// @Test
	public void testRegisteredServices() {
		final Framework framework = moduleComponent.getFrameworkManager().getFramework();
		final BundleContext bundleContext = framework.getBundleContext();
		assertNotNull(bundleContext.getServiceReference(CategoryService.class.getName()));
		assertNotNull(bundleContext.getServiceReference(ContentService.class.getName()));
		assertNotNull(bundleContext.getServiceReference(FileFolderService.class.getName()));
		assertNotNull(bundleContext.getServiceReference(NodeService.class.getName()));
		assertNotNull(bundleContext.getServiceReference(PolicyComponent.class.getName()));
		assertNotNull(bundleContext.getServiceReference(SearchService.class.getName()));
		assertNotNull(bundleContext.getServiceReference(TransactionService.class.getName()));
	}

}
