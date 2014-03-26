package com.github.dynamicextensionsalfresco.workflow.activiti;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.springframework.util.Assert;

/**
 * Activiti workflow execution listener, that can be configured using a <b>componentId</b>.
 * The <b>componentId</b> will identify the {@link org.activiti.engine.delegate.ExecutionListener} component.
 *
 * @author Laurent Van der Linden
 */
public class DelegateExecutionListener extends AbstractDelegate implements ExecutionListener {
    @Override
    public void notify(DelegateExecution execution) throws Exception {
        final String componentId = getComponentId(execution);
        final ExecutionListener listener = getWorkflowTaskRegistry().findExecutionListener(componentId);

        Assert.notNull(listener, String.format("No ExecutionListener found for componentId %s.", componentId));

        listener.notify(execution);
    }
}
