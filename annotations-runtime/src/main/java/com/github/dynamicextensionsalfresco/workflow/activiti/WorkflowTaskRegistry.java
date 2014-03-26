package com.github.dynamicextensionsalfresco.workflow.activiti;

import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.delegate.TaskListener;

import javax.annotation.Nonnull;

/**
 * @author Laurent Van der Linden
 */
public interface WorkflowTaskRegistry {
    public void registerDelegate(@Nonnull String id, @Nonnull JavaDelegate delegate);

    public void unregisterDelegate(@Nonnull String id);

    public JavaDelegate findDelegate(@Nonnull String id);

    public void registerTaskListener(@Nonnull String id, @Nonnull TaskListener taskListener);

    public void unregisterTaskListener(@Nonnull String id);

    public TaskListener findTaskListener(@Nonnull String id);

    public void registerExecutionListener(@Nonnull String id, @Nonnull ExecutionListener executionListener);

    public void unregisterExecutionListener(@Nonnull String id);

    public ExecutionListener findExecutionListener(@Nonnull String id);
}
