package com.github.dynamicextensionsalfresco.osgi.spring;

import java.util.Map;

import com.github.dynamicextensionsalfresco.osgi.FrameworkConfiguration;

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
