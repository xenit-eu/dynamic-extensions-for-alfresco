package com.github.dynamicextensionsalfresco.workflow.activiti;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.util.Assert;

/**
 * Activiti workflow listener, that can be configured using a <b>componentId</b>.
 * The <b>componentId</b> will identify the {@link org.activiti.engine.delegate.TaskListener} component.
 *
 * @author Laurent Van der Linden
 */
public class DelegateTaskListener extends AbstractDelegate implements TaskListener {
    @Override
    public void notify(DelegateTask delegateTask) {
        final String componentId = getComponentId(delegateTask);
        final TaskListener listener = getWorkflowTaskRegistry().findTaskListener(componentId);

        Assert.notNull(listener, String.format("No TaskListener found for componentId %s.", componentId));

        listener.notify(delegateTask);
    }
}
