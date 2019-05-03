package com.github.dynamicextensionsalfresco.workflow.activiti;

import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.delegate.TaskListener;

import javax.annotation.Nonnull;

/**
 * @author Laurent Van der Linden
 */
public interface WorkflowTaskRegistry {
    void registerDelegate(@Nonnull String id, @Nonnull JavaDelegate delegate);

    void unregisterDelegate(@Nonnull String id);

    JavaDelegate findDelegate(@Nonnull String id);

    void registerTaskListener(@Nonnull String id, @Nonnull TaskListener taskListener);

    void unregisterTaskListener(@Nonnull String id);

    TaskListener findTaskListener(@Nonnull String id);

    void registerExecutionListener(@Nonnull String id, @Nonnull ExecutionListener executionListener);

    void unregisterExecutionListener(@Nonnull String id);

    ExecutionListener findExecutionListener(@Nonnull String id);
}
