package com.github.dynamicextensionsalfresco.workflow.activiti;

import org.activiti.engine.delegate.JavaDelegate;

import javax.annotation.Nonnull;

/**
 * @author Laurent Van der Linden
 */
public interface WorkflowTaskRegistry {
    public void registerTask(@Nonnull String id, @Nonnull JavaDelegate delegate);

    public void unRegisterTask(@Nonnull String id);

    public JavaDelegate findDelegate(@Nonnull String id);
}
