package nl.runnable.alfresco.osgi;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

import nl.runnable.alfresco.osgi.spring.SystemPackageConfigurationFactoryBean;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * {@link SystemPackageConfigurationFactoryBean} unit test.
 * 
 * @author Laurens Fridael
 * 
 */
public class SystemPackageConfigurationFactoryBeanTest {

	private SystemPackageConfigurationFactoryBean factoryBean;

	@Before
	public void setup() {
		factoryBean = new SystemPackageConfigurationFactoryBean();
		factoryBean.setResourceLoader(new PathMatchingResourcePatternResolver());
		factoryBean.setConfigurations(Arrays.<Resource> asList(new ClassPathResource(
				"SystemPackageConfigurationFactoryBeanTest-fixture.txt", getClass())));
		factoryBean
				.setConfigurationLocations(Arrays
						.asList("classpath*:/nl/runnable/alfresco/osgi/SystemPackageConfigurationFactoryBeanTest-additional-*.txt"));
		factoryBean.setDefaultVersion("3.4");
	}

	@Test
	public void testGetObjectType() {
		assertTrue(Set.class.isAssignableFrom(factoryBean.getObjectType()));
	}

	@Test
	public void testIsSingleton() {
		assertTrue(factoryBean.isSingleton());
	}

	@Test
	public void testGetObject() throws IOException {
		final Set<SystemPackage> systemPackages = factoryBean.getObject();
		assertEquals(3, systemPackages.size());
		assertTrue(systemPackages.contains(new SystemPackage("org.alfresco.service.cmr.repository", "3.2")));
		assertTrue(systemPackages.contains(new SystemPackage("org.alfresco.service.cmr.search", "3.4")));
		assertTrue(systemPackages.contains(new SystemPackage("org.alfresco.service.transaction", "3.4")));
	}
}
