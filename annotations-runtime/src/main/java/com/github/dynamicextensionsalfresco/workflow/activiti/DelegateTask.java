package com.github.dynamicextensionsalfresco.workflow.activiti;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.el.Expression;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * Activiti workflow task delegate that can be configured using a taskid.
 * The taskid will delegate the invocation the a registered task.
 *
 * @author Laurent Van der Linden
 */
public class DelegateTask implements JavaDelegate {
    private Expression taskId;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        ProcessEngineConfigurationImpl config = Context.getProcessEngineConfiguration();
        Assert.notNull(config, "No ProcessEngineConfiguration found in active context");
        final Map<Object,Object> beans = config.getBeans();
        WorkflowTaskRegistry workflowTaskRegistry = (WorkflowTaskRegistry) beans.get(DefaultWorkflowTaskRegistry.BEAN_NAME);
        Assert.notNull(taskId, "no taskId set for BundleTask");
        final String taskIdValue = (String) taskId.getValue(execution);
        final JavaDelegate delegate = workflowTaskRegistry.findDelegate(taskIdValue);
        Assert.notNull(delegate, String.format("no BundleTask found for taskId <%s>", taskIdValue));
        delegate.execute(execution);
    }

    public void setTaskId(Expression taskId) {
        this.taskId = taskId;
    }
}
