package nl.runnable.alfresco.osgi;

import static java.util.Arrays.*;
import static org.junit.Assert.*;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Constants;

public class FrameworkConfigurationTest {

	private FrameworkConfiguration frameworkConfiguration;

	private File storageDirectory;

	@Before
	public void setup() {
		frameworkConfiguration = new FrameworkConfiguration();

		storageDirectory = new File("tmp");
		frameworkConfiguration.setStorageDirectory(storageDirectory);

		frameworkConfiguration.setFlushBundleCacheOnFirstInit(true);

		frameworkConfiguration.setCoreSystemPackages(new HashSet<SystemPackage>(asList(new SystemPackage(
				"core-package", "1.0"))));

		frameworkConfiguration.setAdditionalSystemPackages(new LinkedHashSet<SystemPackage>(asList(new SystemPackage(
				"additional-package1", "1.0"), new SystemPackage("additional-package2", "2.0"))));
	}

	@Test
	public void testToMap() {
		final Map<String, String> map = frameworkConfiguration.toMap();
		assertEquals(storageDirectory.getAbsolutePath(), map.get(Constants.FRAMEWORK_STORAGE));
		assertEquals(Constants.FRAMEWORK_STORAGE_CLEAN_ONFIRSTINIT, map.get(Constants.FRAMEWORK_STORAGE_CLEAN));
		assertEquals("core-package;version=1.0", map.get(Constants.FRAMEWORK_SYSTEMPACKAGES));
		assertEquals("additional-package1;version=1.0,additional-package2;version=2.0",
				map.get(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA));
	}

}
