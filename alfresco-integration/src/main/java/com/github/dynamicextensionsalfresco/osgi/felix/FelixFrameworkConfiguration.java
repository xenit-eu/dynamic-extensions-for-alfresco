package com.github.dynamicextensionsalfresco.osgi.felix;

import java.util.List;
import java.util.Map;

import com.github.dynamicextensionsalfresco.osgi.FrameworkConfiguration;

import org.osgi.framework.BundleActivator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

/**
 * {@link FrameworkConfiguration} subclass with configuration settings specific to Felix.
 * 
 * @author Laurens Fridael
 * 
 */
public class FelixFrameworkConfiguration extends FrameworkConfiguration {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private List<BundleActivator> systemBundleActivators;

	public void setSystemBundleActivators(final List<BundleActivator> systemBundleActivators) {
		this.systemBundleActivators = systemBundleActivators;
	}

	protected List<BundleActivator> getSystemBundleActivators() {
		return systemBundleActivators;
	}

	@Override
	public Map<String, String> toMap() {
		final Map<String, String> map = super.toMap();
		if (CollectionUtils.isEmpty(getSystemBundleActivators()) == false) {
			// TODO: Determine how we can support this in Felix 4.0.0.
			logger.warn("Configuring system bundle activators is currently not supported. This configuration will be ignored.");
			// map.put(FelixConstants.SYSTEMBUNDLE_ACTIVATORS_PROP, getSystemBundleActivators());
		}
		return map;
	}
}
