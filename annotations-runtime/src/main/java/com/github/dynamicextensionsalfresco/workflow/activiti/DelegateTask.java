package com.github.dynamicextensionsalfresco.workflow.activiti;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.util.Assert;

/**
 * Activiti workflow task delegate, that can be configured using a <b>componentId</b>.
 * The <b>componentId</b> will identify the {@link JavaDelegate} component.
 *
 * @author Laurent Van der Linden
 */
public class DelegateTask extends AbstractDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        final String componentId = getComponentId(execution);
        final JavaDelegate delegate = getWorkflowTaskRegistry().findDelegate(componentId);
        Assert.notNull(delegate, String.format("No JavaDelegate found for componentId %s.", componentId));
        delegate.execute(execution);
    }
}
