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

package nl.runnable.alfresco.blueprint;

import org.eclipse.gemini.blueprint.context.DelegatedExecutionOsgiBundleApplicationContext;
import org.eclipse.gemini.blueprint.extender.OsgiApplicationContextCreator;
import org.eclipse.gemini.blueprint.extender.support.ApplicationContextConfiguration;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.springframework.util.StringUtils;

/**
 * {@link OsgiApplicationContextCreator} for Dynamic Extensions.
 * <p>
 * This implementation creates a {@link DynamicExtensionsApplicationContext} for Spring-powered {@link Bundle}s.
 * 
 * @author Laurens Fridael
 * 
 */
public class DynamicExtensionsApplicationContextCreator implements OsgiApplicationContextCreator {

	/* Configuration */

	private String modelLocationPattern;

	/* Operations */

	@Override
	public DelegatedExecutionOsgiBundleApplicationContext createApplicationContext(final BundleContext bundleContext)
			throws Exception {
		final Bundle bundle = bundleContext.getBundle();
		/*
		 * WARNING: Avoid creating an instance of
		 * org.eclipse.gemini.blueprint.extender.internal.blueprint.activator.support.BlueprintContainerConfig, since
		 * this package is not exported by the extender Bundle. You should use
		 * org.eclipse.gemini.blueprint.extender.support.ApplicationContextConfiguration instead.
		 * 
		 * TODO: this may have been fixed in Blueprint 1.0.1.RELEASE.
		 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=379384
		 */
		final ApplicationContextConfiguration config = new ApplicationContextConfiguration(bundle);
		String[] configurationLocations = null;
		if (config.isSpringPoweredBundle()) {
			configurationLocations = config.getConfigurationLocations();
		}
		final DynamicExtensionsApplicationContext applicationContext = new DynamicExtensionsApplicationContext(
				configurationLocations);
		applicationContext.setBundleContext(bundleContext);
		applicationContext.setPublishContextAsService(config.isPublishContextAsService());
		if (StringUtils.hasText(getModelLocationPattern())) {
			applicationContext.setModelLocationPattern(getModelLocationPattern());
		}
		return applicationContext;

	}

	/* Configuration */

	public void setModelLocationPattern(final String modelLocationPattern) {
		this.modelLocationPattern = modelLocationPattern;
	}

	protected String getModelLocationPattern() {
		return modelLocationPattern;
	}

}
