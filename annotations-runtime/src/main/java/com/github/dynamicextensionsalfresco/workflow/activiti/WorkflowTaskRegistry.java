package com.github.dynamicextensionsalfresco.workflow.activiti;

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

    public void registerListener(@Nonnull String id, @Nonnull TaskListener taskListener);

    public void unregisterListener(@Nonnull String id);

    public TaskListener findListener(@Nonnull String id);
}
