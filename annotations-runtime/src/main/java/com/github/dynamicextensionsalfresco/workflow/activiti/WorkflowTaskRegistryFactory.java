package com.github.dynamicextensionsalfresco.workflow.activiti;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

/**
 * Detect Activiti in the classpath and only register bean in that case.
 * Allows for Alfresco 3.4 support
 *
 * @author Laurent Van der Linden
 */
public class WorkflowTaskRegistryFactory implements FactoryBean<Object> {
    private final static Logger logger = LoggerFactory.getLogger(WorkflowTaskRegistryFactory.class);

    private Object implementation;

    public WorkflowTaskRegistryFactory() {
        try {
            Class.forName("org.activiti.engine.delegate.JavaDelegate");
            final Class<?> implementationClass = Class.forName("com.github.dynamicextensionsalfresco.workflow.activiti.DefaultWorkflowTaskRegistry");
            implementation = implementationClass.newInstance();
        } catch (Throwable e) {
            logger.info("Activiti support disabled");
        }
    }

    @Override
    public Object getObject() throws Exception {
        if (implementation != null) {
            return implementation;
        } else {
            return new Object();
        }
    }

    @Override
    public Class<?> getObjectType() {
        if (implementation == null) {
            return Object.class;
        } else {
            try {
                return Class.forName("com.github.dynamicextensionsalfresco.workflow.activiti.WorkflowTaskRegistry");
            } catch (NoClassDefFoundError e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
