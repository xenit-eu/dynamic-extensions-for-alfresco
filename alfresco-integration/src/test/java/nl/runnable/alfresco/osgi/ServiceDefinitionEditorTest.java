package nl.runnable.alfresco.osgi;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

/**
 * {@link ServiceDefinitionEditor} test.
 * 
 * @author Laurens Fridael
 * 
 */
public class ServiceDefinitionEditorTest {

	private ServiceDefinitionEditor propertyEditor;

	@Before
	public void setup() {
		propertyEditor = new ServiceDefinitionEditor();
	}

	@Test
	public void testWithBeanNamesServiceNamesAndServiceTypeAndPlatformVersion() {
		propertyEditor.setAsText("nodeService,NodeService:custom.NodeService1,custom.NodeService2:low-level:4.0");
		final ServiceDefinition serviceDefinition = (ServiceDefinition) propertyEditor.getValue();
		assertEquals("nodeService", serviceDefinition.getBeanNames().get(0));
		assertEquals("NodeService", serviceDefinition.getBeanNames().get(1));
		assertEquals(Arrays.asList("custom.NodeService1", "custom.NodeService2"), serviceDefinition.getServiceNames());
		assertEquals("low-level", serviceDefinition.getServiceType());
		assertEquals("4.0", serviceDefinition.getPlatformVersion());
	}

	@Test
	public void testWithBeanNamesServiceNamesAndServiceType() {
		propertyEditor.setAsText("nodeService,NodeService:custom.NodeService1,custom.NodeService2:low-level");
		final ServiceDefinition serviceDefinition = (ServiceDefinition) propertyEditor.getValue();
		assertEquals("nodeService", serviceDefinition.getBeanNames().get(0));
		assertEquals("NodeService", serviceDefinition.getBeanNames().get(1));
		assertEquals(Arrays.asList("custom.NodeService1", "custom.NodeService2"), serviceDefinition.getServiceNames());
		assertEquals("low-level", serviceDefinition.getServiceType());
	}

	@Test
	public void testWithBeanNamesAndServiceNames() {
		propertyEditor.setAsText("nodeService,NodeService:custom.NodeService1,custom.NodeService2");
		final ServiceDefinition serviceDefinition = (ServiceDefinition) propertyEditor.getValue();
		assertEquals("nodeService", serviceDefinition.getBeanNames().get(0));
		assertEquals("NodeService", serviceDefinition.getBeanNames().get(1));
		assertEquals(Arrays.asList("custom.NodeService1", "custom.NodeService2"), serviceDefinition.getServiceNames());
	}
}
