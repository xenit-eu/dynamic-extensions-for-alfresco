package com.github.dynamicextensionsalfresco.workflow.activiti;

import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.delegate.TaskListener;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Extension workflow task registry.
 *
 * @author Laurent Van der Linden
 */
public class DefaultWorkflowTaskRegistry implements WorkflowTaskRegistry {
    public final static String BEAN_NAME = "osgi.container.WorkflowTaskRegistry";

    private final Map<String, JavaDelegate> delegates = new ConcurrentHashMap<String, JavaDelegate>();

    private final Map<String, TaskListener> taskListeners = new ConcurrentHashMap<String, TaskListener>();

    private final Map<String, ExecutionListener> executionListeners = new ConcurrentHashMap<String, ExecutionListener>();

    @Override
    public void registerDelegate(@Nonnull String id, @Nonnull JavaDelegate delegate) {
        final JavaDelegate existing = delegates.put(id, delegate);
        if (existing != null) {
            throw new IllegalStateException(String.format("overwrite of existing delegate using id <%s>", id));
        }
    }

    @Override
    public void unregisterDelegate(@Nonnull String id) {
        delegates.remove(id);
    }

    @Override
    public JavaDelegate findDelegate(@Nonnull String id) {
        return delegates.get(id);
    }

    @Override
    public void registerTaskListener(@Nonnull String id, @Nonnull TaskListener taskListener) {
        final TaskListener existing = taskListeners.put(id, taskListener);
        if (existing != null) {
            throw new IllegalStateException(String.format("overwrite of existing execution listener using id <%s>", id));
        }
    }

    @Override
    public void unregisterTaskListener(@Nonnull String id) {
        taskListeners.remove(id);
    }

    @Override
    public TaskListener findTaskListener(@Nonnull String id) {
        return taskListeners.get(id);
    }

    @Override
    public void registerExecutionListener(@Nonnull String id, @Nonnull ExecutionListener executionListener) {
        final ExecutionListener existing = executionListeners.put(id, executionListener);
        if (existing != null) {
            throw new IllegalStateException(String.format("overwrite of existing execution listener using id <%s>", id));
        }
    }

    @Override
    public void unregisterExecutionListener(@Nonnull String id) {
        executionListeners.remove(id);
    }

    @Override
    public ExecutionListener findExecutionListener(@Nonnull String id) {
        return executionListeners.get(id);
    }
}
