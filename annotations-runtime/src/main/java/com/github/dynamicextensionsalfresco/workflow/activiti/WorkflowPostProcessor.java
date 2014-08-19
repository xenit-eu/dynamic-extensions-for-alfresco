package com.github.dynamicextensionsalfresco.workflow.activiti;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

/**
 * We need to override the default services activiti exposes to it's workflows.
 * Here we add our workflowTaskRegistry so that the {@link DelegateTask} can access it.
 *
 * @author Laurent Van der Linden
 */
public class WorkflowPostProcessor implements BeanPostProcessor, ApplicationContextAware {
    private ApplicationContext applicationContext;

    @SuppressWarnings("unchecked")
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (applicationContext.containsBean("activitiBeanRegistry")) {
            final Object workflowTaskRegistry = applicationContext.getBean("osgi.container.WorkflowTaskRegistry");
            final Map<String,Object> activitiBeanRegistry = (Map<String, Object>) applicationContext.getBean("activitiBeanRegistry");
            if (beanName.equals("activitiProcessEngineConfiguration")) {
                activitiBeanRegistry.put(DefaultWorkflowTaskRegistry.BEAN_NAME, workflowTaskRegistry);
            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
