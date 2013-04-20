package nl.runnable.alfresco.osgi.felix;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;
import java.util.Map;

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

	private FelixFrameworkConfiguration configuration;

	@Before
	public void setup() {
		configuration = new FelixFrameworkConfiguration();
	}

	@Test
	public void testConversionToMap() {
		// Configuration should be blank initially.
		// HACK: Disabled this
		// assertTrue(configuration.toMap().isEmpty());

		final File storageDirectory = new File("tmp");
		final BundleActivator simpleBundleActivator = new SimpleBundleActivator();

		configuration.setStorageDirectory(storageDirectory);
		configuration.setFlushBundleCacheOnFirstInit(true);
		configuration.setSystemBundleActivators(Arrays.<BundleActivator> asList(simpleBundleActivator));

		final Map<String, String> map = configuration.toMap();
		assertEquals(storageDirectory.getAbsolutePath(), map.get(Constants.FRAMEWORK_STORAGE));
		assertEquals(Constants.FRAMEWORK_STORAGE_CLEAN_ONFIRSTINIT, map.get(Constants.FRAMEWORK_STORAGE_CLEAN));
	}
}
