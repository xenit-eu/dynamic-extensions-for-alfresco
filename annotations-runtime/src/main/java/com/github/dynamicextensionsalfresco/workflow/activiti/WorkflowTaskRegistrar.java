package com.github.dynamicextensionsalfresco.workflow.activiti;

import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.delegate.TaskListener;
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
 * Detects workflow tasks from Spring beans implementing the {@link DelegateTask} or {@link TaskListener} interface.
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
            workflowTaskRegistry.registerDelegate(entry.getKey(), entry.getValue());
            logger.debug("Register Bundle JavaDelegate {} -> {}.", entry.getKey(), entry.getValue().getClass());
        }

        final Map<String,TaskListener> listeners = applicationContext.getBeansOfType(TaskListener.class);
        for (Map.Entry<String, TaskListener> entry : listeners.entrySet()) {
            workflowTaskRegistry.registerListener(entry.getKey(), entry.getValue());
            logger.debug("Register Bundle TaskListener {} -> {}.", entry.getKey(), entry.getValue().getClass());
        }
    }

    @Override
    public void destroy() throws Exception {
        for (String key : applicationContext.getBeansOfType(JavaDelegate.class).keySet()) {
            workflowTaskRegistry.unregisterDelegate(key);
        }

        for (String key : applicationContext.getBeansOfType(TaskListener.class).keySet()) {
            workflowTaskRegistry.unregisterListener(key);
        }
    }
}
