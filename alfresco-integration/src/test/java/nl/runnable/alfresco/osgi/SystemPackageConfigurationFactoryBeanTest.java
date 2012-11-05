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

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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
		assertTrue(List.class.isAssignableFrom(factoryBean.getObjectType()));
	}

	@Test
	public void testIsSingleton() {
		assertTrue(factoryBean.isSingleton());
	}

	@Test
	public void testGetObject() throws IOException {
		final List<SystemPackage> systemPackages = factoryBean.getObject();
		assertEquals(3, systemPackages.size());
		assertEquals(systemPackages.get(0).getName(), "org.alfresco.service.cmr.repository");
		assertEquals(systemPackages.get(0).getVersion(), "3.2");
		assertEquals(systemPackages.get(1).getName(), "org.alfresco.service.cmr.search");
		assertEquals(systemPackages.get(1).getVersion(), "3.4");
		assertEquals(systemPackages.get(2).getName(), "org.alfresco.service.transaction");
		assertEquals(systemPackages.get(2).getVersion(), "3.4");
	}
}
