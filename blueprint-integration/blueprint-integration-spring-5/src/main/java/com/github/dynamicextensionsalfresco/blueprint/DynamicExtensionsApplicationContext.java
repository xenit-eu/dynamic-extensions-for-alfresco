package com.github.dynamicextensionsalfresco.blueprint;

import com.github.dynamicextensionsalfresco.blueprint.spring5.Spring5OsgiAutowireBeanFactory;
import org.alfresco.util.VersionNumber;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;

public class DynamicExtensionsApplicationContext extends DynamicExtensionsApplicationContextBase {
    public DynamicExtensionsApplicationContext(String[] configurationLocations, ApplicationContext parent) {
        super(configurationLocations, parent);
    }

    @Override
    protected DefaultListableBeanFactory createVersionSpecificBeanFactory(VersionNumber version) {
        return new Spring5OsgiAutowireBeanFactory(
                this.getInternalParentBeanFactory(), this.getBundleContext());
    }
}
