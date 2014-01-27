package com.github.dynamicextensionsalfresco.workflow.activiti;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.util.Assert;

/**
 * Activiti workflow task listener, that can be configured using a <b>componentId</b>.
 * The <b>componentId</b> will identify the {@link TaskListener} component.
 *
 * @author Laurent Van der Linden
 */
public class DelegateListener extends AbstractDelegate implements TaskListener {
    @Override
    public void notify(DelegateTask delegateTask) {
        final String componentId = getComponentId(delegateTask);
        final TaskListener listener = getWorkflowTaskRegistry().findListener(componentId);

        Assert.notNull(listener, String.format("No JavaDelegate found for componentId %s.", componentId));

        listener.notify(delegateTask);
    }
}
