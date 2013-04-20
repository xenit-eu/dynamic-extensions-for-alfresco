package nl.runnable.alfresco.osgi.felix;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;
import java.util.Map;

import nl.runnable.alfresco.osgi.Configuration;

import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

/**
 * {@link FelixFrameworkConfiguration} unit test.
 * 
 * @author Laurens Fridael
 * 
 */
public class FelixFrameworkConfigurationTest {

	private static final class SimpleBundleActivator implements BundleActivator {
		@Override
		public void start(final BundleContext context) throws Exception {
		}

		@Override
		public void stop(final BundleContext context) throws Exception {
		}

	}

	private FelixFrameworkConfiguration frameworkConfiguration;

	@Before
	public void setup() {
		frameworkConfiguration = new FelixFrameworkConfiguration();
	}

	@Test
	public void testConversionToMap() {
		// Configuration should be blank initially.
		// HACK: Disabled this
		// assertTrue(frameworkConfiguration.toMap().isEmpty());

		final File storageDirectory = new File("tmp");
		final Configuration configuration = new Configuration();
		configuration.setStorageDirectory(storageDirectory);
		frameworkConfiguration.setConfiguration(configuration);
		frameworkConfiguration.setFlushBundleCacheOnFirstInit(true);
		final BundleActivator simpleBundleActivator = new SimpleBundleActivator();
		frameworkConfiguration.setSystemBundleActivators(Arrays.<BundleActivator> asList(simpleBundleActivator));

		final Map<String, String> map = frameworkConfiguration.toMap();
		assertEquals(storageDirectory.getAbsolutePath(), map.get(Constants.FRAMEWORK_STORAGE));
		assertEquals(Constants.FRAMEWORK_STORAGE_CLEAN_ONFIRSTINIT, map.get(Constants.FRAMEWORK_STORAGE_CLEAN));
	}
}
