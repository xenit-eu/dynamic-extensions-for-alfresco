package com.github.dynamicextensionsalfresco.blueprint;

import com.github.dynamicextensionsalfresco.blueprint.spring3.Spring3OsgiAutowireBeanFactory;
import org.alfresco.util.VersionNumber;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;

public class DynamicExtensionsApplicationContext extends DynamicExtensionsApplicationContextBase {
    public DynamicExtensionsApplicationContext(String[] configurationLocations, ApplicationContext parent) {
        super(configurationLocations, parent);
    }

    @Override
    protected DefaultListableBeanFactory createVersionSpecificBeanFactory(VersionNumber version) {
        return new Spring3OsgiAutowireBeanFactory(
                this.getInternalParentBeanFactory(), this.getBundleContext());
    }
}
