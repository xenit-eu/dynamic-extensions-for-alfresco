package com.github.dynamicextensionsalfresco.blueprint;

import com.github.dynamicextensionsalfresco.blueprint.spring5.Spring5HostApplicationContext;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.springframework.context.ApplicationContext;

public class DynamicExtensionsApplicationContextCreator extends DynamicExtensionsApplicationContextCreatorBase {
    @Override
    protected DynamicExtensionsApplicationContextBase createNewInstance(
            String[] configurationLocations, ApplicationContext hostApplicationContext) {
        return new DynamicExtensionsApplicationContext(
                configurationLocations, hostApplicationContext);
    }

    @Override
    protected ApplicationContext getSpringSpecificHostApplicationContext(
            BundleContext bundleContext, ServiceReference<?> serviceReference) {
        	return new Spring5HostApplicationContext(
						(ApplicationContext) bundleContext.getService(serviceReference));
    }
}
