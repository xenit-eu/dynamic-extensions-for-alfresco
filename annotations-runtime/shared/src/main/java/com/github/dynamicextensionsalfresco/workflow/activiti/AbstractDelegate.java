package com.github.dynamicextensionsalfresco.workflow.activiti;

import org.activiti.engine.delegate.VariableScope;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.el.Expression;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * Support class for Workflow delegates: provides access to {@link WorkflowTaskRegistry}.
 *
 * @author Laurent Van der Linden
 */
public abstract class AbstractDelegate {
    private Expression componentId;

    protected WorkflowTaskRegistry getWorkflowTaskRegistry() {
        ProcessEngineConfigurationImpl config = Context.getProcessEngineConfiguration();
        Assert.notNull(config, "No ProcessEngineConfiguration found in active context.");
        final Map<Object,Object> beans = config.getBeans();
        return (WorkflowTaskRegistry) beans.get(DefaultWorkflowTaskRegistry.BEAN_NAME);
    }

    protected String getComponentId(VariableScope variableScope) {
        final Object value = componentId.getValue(variableScope);
        Assert.isInstanceOf(String.class, "componentId should be a String");
        return (String) value;
    }

    public void setComponentId(Expression componentId) {
        this.componentId = componentId;
    }
}
