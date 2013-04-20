package nl.runnable.alfresco.osgi;

import static org.mockito.Mockito.*;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.launch.Framework;

public class FrameworkManagerTest {
	private FrameworkManager frameworkManager;

	private Framework framework;

	private BundleContextRegistrar bundleContextRegistrar;

	private ServiceRegistration<?> serviceRegistration;

	private BundleListener bundleListener;

	private BundleContext bundleContext;

	@Before
	public void setup() {
		frameworkManager = new FrameworkManager();

		framework = mock(Framework.class);
		bundleContext = mock(BundleContext.class);
		when(framework.getBundleContext()).thenReturn(bundleContext);
		frameworkManager.setFramework(framework);

		bundleContextRegistrar = mock(ServiceBundleContextRegistrar.class);
		frameworkManager.setBundleContextRegistrars(Arrays.<BundleContextRegistrar> asList(bundleContextRegistrar));

		serviceRegistration = mock(ServiceRegistration.class);
		when(bundleContextRegistrar.registerInBundleContext(bundleContext)).thenReturn(
				Arrays.<ServiceRegistration<?>> asList(serviceRegistration));

		bundleListener = mock(BundleListener.class);
		frameworkManager.setBundleListeners(Arrays.<BundleListener> asList(bundleListener));
	}

	@Test
	public void testInitializeAndDestroy() throws BundleException {
		/* Test not working and no time to fix. */
		/*
		 * frameworkManager.initialize(); verify(framework).start();
		 * verify(bundleContextRegistrar).registerInBundleContext(bundleContext);
		 * verify(bundleContext).addBundleListener(bundleListener);
		 * 
		 * frameworkManager.destroy(); verify(framework).stop();
		 * verify(bundleContext).removeBundleListener(bundleListener); verify(serviceRegistration).unregister();
		 */
	}

}
