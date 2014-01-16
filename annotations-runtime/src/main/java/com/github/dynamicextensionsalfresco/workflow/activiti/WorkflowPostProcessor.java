package com.github.dynamicextensionsalfresco.workflow.activiti;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.Map;

/**
 * We need to override the default services activiti exposes to it's workflows.
 * Here we add our workflowTaskRegistry so that the {@link DelegateTask} can access it.
 *
 * @author Laurent Van der Linden
 */
public class WorkflowPostProcessor implements BeanPostProcessor {
    private WorkflowTaskRegistry workflowTaskRegistry;
    private Map<String,Object> activitiBeanRegistry;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (beanName.equals("activitiProcessEngineConfiguration")) {
            activitiBeanRegistry.put(DefaultWorkflowTaskRegistry.BEAN_NAME, workflowTaskRegistry);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    public void setWorkflowTaskRegistry(WorkflowTaskRegistry workflowTaskRegistry) {
        this.workflowTaskRegistry = workflowTaskRegistry;
    }

    public void setActivitiBeanRegistry(Map<String, Object> activitiBeanRegistry) {
        this.activitiBeanRegistry = activitiBeanRegistry;
    }
}
