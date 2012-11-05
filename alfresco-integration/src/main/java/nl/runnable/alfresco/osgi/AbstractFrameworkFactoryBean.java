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

import java.util.Map;

import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;
import org.springframework.beans.factory.FactoryBean;

/**
 * Abstract base class for FactoryBeans that create {@link Framework}s. Subclasses must provide the
 * implementation-specific {@link FrameworkFactory} used to instantiate the Framework.
 * 
 * @author Laurens Fridael
 * 
 */
public abstract class AbstractFrameworkFactoryBean implements FactoryBean<Framework> {

	private FrameworkConfiguration frameworkConfiguration;

	private Framework framework;

	public void setFrameworkConfiguration(final FrameworkConfiguration configuration) {
		this.frameworkConfiguration = configuration;
	}

	/**
	 * Provides the Framework configuration settings as a Map suitable for use with
	 * <code>FrameworkFactory#newFramework(Map)</code>.
	 * 
	 * @return The Framework configuration or null if none has been specified.
	 * @see FrameworkConfiguration#toMap()
	 */
	protected Map<String, String> getFrameworkConfigurationMap() {
		if (frameworkConfiguration != null) {
			return frameworkConfiguration.toMap();
		} else {
			return null;
		}
	}

	@Override
	public Class<? extends Framework> getObjectType() {
		return Framework.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public final Framework getObject() {
		if (framework == null) {
			framework = createFramework();
		}
		return framework;
	}

	protected Framework createFramework() {
		final Map<String, String> configuration = getFrameworkConfigurationMap();
		return getFrameworkFactory().newFramework(configuration);
	}

	/**
	 * Obtains the FrameworkFactory that will be used to create the Framework.
	 * 
	 * @return The FrameworkFactory.
	 */
	protected abstract FrameworkFactory getFrameworkFactory();

}
