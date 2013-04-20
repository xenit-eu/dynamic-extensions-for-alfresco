package nl.runnable.alfresco.osgi;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import nl.runnable.alfresco.osgi.spring.ServiceDefinitionConfigurationFactoryBean;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * {@link ServiceDefinitionConfigurationFactoryBean} unit test.
 * 
 * @author Laurens Fridael
 * 
 */
public class ServiceDefinitionConfigurationFactoryBeanTest {

	private ServiceDefinitionConfigurationFactoryBean factoryBean;

	@Before
	public void setup() {
		factoryBean = new ServiceDefinitionConfigurationFactoryBean();
		factoryBean.setResourceLoader(new PathMatchingResourcePatternResolver(getClass().getClassLoader()));
		factoryBean.setConfigurations(Arrays.<Resource> asList(new ClassPathResource(
				"ServiceDefinitionConfigurationFactoryBeanTest-fixture.txt", getClass())));
		factoryBean
				.setConfigurationLocations(Arrays
						.asList("classpath*:/nl/runnable/alfresco/osgi/ServiceDefinitionConfigurationFactoryBeanTest-additonal-*.txt"));
	}

	@Test
	public void testGetObjectType() {
		assertTrue(List.class.isAssignableFrom(factoryBean.getObjectType()));
	}

	@Test
	public void testIsSingleton() {
		assertTrue(factoryBean.isSingleton());
	}

	@Test
	public void testGetObject() throws IOException {
		final List<ServiceDefinition> serviceDefinitions = factoryBean.getObject();
		assertEquals(3, serviceDefinitions.size());
		assertEquals("namespaceService", serviceDefinitions.get(0).getBeanNames().get(0));
		assertEquals("org.alfresco.service.namespace.NamespaceService", serviceDefinitions.get(0).getServiceNames()
				.get(0));
		assertEquals("org.alfresco.service.namespace.NamespacePrefixResolver", serviceDefinitions.get(0)
				.getServiceNames().get(1));
		assertEquals("nodeService", serviceDefinitions.get(1).getBeanNames().get(0));
		assertEquals("org.alfresco.service.cmr.repository.NodeService", serviceDefinitions.get(1).getServiceNames()
				.get(0));
		assertEquals("categoryService", serviceDefinitions.get(2).getBeanNames().get(0));
		assertEquals("org.alfresco.service.cmr.search.CategoryService", serviceDefinitions.get(2).getServiceNames()
				.get(0));

	}

}
