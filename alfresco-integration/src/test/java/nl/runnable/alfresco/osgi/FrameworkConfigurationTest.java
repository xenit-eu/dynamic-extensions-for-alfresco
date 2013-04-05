/*
Copyright (c) 2012, Runnable
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
 * Neither the name of Runnable nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package nl.runnable.alfresco.osgi;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;
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

		frameworkConfiguration.setCoreSystemPackages(Arrays.asList(new SystemPackage("core-package", "1.0")));

		frameworkConfiguration.setAdditionalSystemPackages(Arrays.asList(
				new SystemPackage("additional-package1", "1.0"), new SystemPackage("additional-package2", "2.0")));
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
