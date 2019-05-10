package com.github.dynamicextensionsalfresco.workflow.activiti;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.TaskListener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

@RunWith(MockitoJUnitRunner.class)
public class WorkflowTaskRegistrarTest {


    @Mock
    private WorkflowTaskRegistry workflowTaskRegistry;
    @Mock
    private ApplicationContext applicationContext;

    private WorkflowTaskRegistrar workflowTaskRegistrar;

    private static final String BEAN_ID_LISTENER = "TaskAndExceptionListener";
    private TaskAndExceptionListener listener = new TaskAndExceptionListener();

    @Before
    public void setup() {
        workflowTaskRegistrar = new WorkflowTaskRegistrar(new HashMap<>(), workflowTaskRegistry);
        workflowTaskRegistrar.setApplicationContext(applicationContext);

        when(applicationContext.getBeansOfType(TaskListener.class))
                .thenReturn(Collections.singletonMap(BEAN_ID_LISTENER, listener));
        when(applicationContext.getBeansOfType(ExecutionListener.class))
                .thenReturn(Collections.singletonMap(BEAN_ID_LISTENER, listener));
    }

    @Test
    public void afterPropertiesSet_beanImplementingMultipleActivitiListeners_correctlyRegistered() {
        // https://github.com/xenit-eu/dynamic-extensions-for-alfresco/issues/199

        workflowTaskRegistrar.afterPropertiesSet();

        verify(workflowTaskRegistry).registerExecutionListener(BEAN_ID_LISTENER, listener);
        verify(workflowTaskRegistry).registerTaskListener(BEAN_ID_LISTENER, listener);
    }

    @Test
    public void destroy_beanImplementingMultipleActivitiListeners_correctlyRemoved() {
        workflowTaskRegistrar.destroy();

        verify(workflowTaskRegistry).unregisterExecutionListener(BEAN_ID_LISTENER);
        verify(workflowTaskRegistry).unregisterTaskListener(BEAN_ID_LISTENER);
    }

    /**
     * Class for testing purposes that implements multiple Activiti listener interfaces
     */
    private class TaskAndExceptionListener implements TaskListener, ExecutionListener {

        @Override
        public void notify(DelegateTask delegateTask) {

        }

        @Override
        public void notify(DelegateExecution execution) {

        }
    }

}