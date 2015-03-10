package com.github.dynamicextensionsalfresco.event.impl;

import com.github.dynamicextensionsalfresco.event.EventBus;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * @author Laurent Van der Linden
 */
public class Activator implements BundleActivator {
	@Override
	public void start(BundleContext context) throws Exception {
		context.registerService(EventBus.class, new DefaultEventBus(context), null);
	}

	@Override
	public void stop(BundleContext context) throws Exception {}
}
