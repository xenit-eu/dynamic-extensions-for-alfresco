package com.github.dynamicextensionsalfresco.workflow.activiti;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.el.Expression;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * Activiti workflow task delegate, that can be configured using a <b>taskId</b>.
 * The <b>taskId</b> will delegate the invocation to a registered task.
 * <br/>A registered task is any Spring component that implements {@link JavaDelegate}
 *
 * @author Laurent Van der Linden
 */
public class DelegateTask implements JavaDelegate {
    private Expression taskId;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        ProcessEngineConfigurationImpl config = Context.getProcessEngineConfiguration();
        Assert.notNull(config, "No ProcessEngineConfiguration found in active context.");
        final Map<Object,Object> beans = config.getBeans();
        WorkflowTaskRegistry workflowTaskRegistry = (WorkflowTaskRegistry) beans.get(DefaultWorkflowTaskRegistry.BEAN_NAME);
        Assert.notNull(taskId, "No taskId set for BundleTask.");
        final String taskIdValue = (String) taskId.getValue(execution);
        final JavaDelegate delegate = workflowTaskRegistry.findDelegate(taskIdValue);
        Assert.notNull(delegate, String.format("No BundleTask found for taskId %s.", taskIdValue));
        delegate.execute(execution);
    }

    public void setTaskId(Expression taskId) {
        this.taskId = taskId;
    }
}
