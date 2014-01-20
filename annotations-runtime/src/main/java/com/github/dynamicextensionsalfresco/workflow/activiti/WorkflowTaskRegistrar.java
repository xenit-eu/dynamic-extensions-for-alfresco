package com.github.dynamicextensionsalfresco.workflow.activiti;

import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

/**
 * Detects workflow tasks from Spring beans implementing the {@link DelegateTask} interface.
 *
 * @author Laurent Van der Linden
 */
public class WorkflowTaskRegistrar implements InitializingBean, ApplicationContextAware, DisposableBean {
    private final static Logger logger = LoggerFactory.getLogger(WorkflowTaskRegistrar.class);

    private ApplicationContext applicationContext;
    private WorkflowTaskRegistry workflowTaskRegistry;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Autowired
    public void setWorkflowTaskRegistry(WorkflowTaskRegistry workflowTaskRegistry) {
        this.workflowTaskRegistry = workflowTaskRegistry;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        final Map<String,JavaDelegate> delegates = applicationContext.getBeansOfType(JavaDelegate.class);
        for (Map.Entry<String, JavaDelegate> entry : delegates.entrySet()) {
            workflowTaskRegistry.registerTask(entry.getKey(), entry.getValue());
            logger.debug("Register Bundle WorkflowTask {} -> {}.", entry.getKey(), entry.getValue().getClass());
        }
    }

    @Override
    public void destroy() throws Exception {
        final Map<String,JavaDelegate> delegates = applicationContext.getBeansOfType(JavaDelegate.class);
        for (String key : delegates.keySet()) {
            workflowTaskRegistry.unRegisterTask(key);
        }
    }
}
