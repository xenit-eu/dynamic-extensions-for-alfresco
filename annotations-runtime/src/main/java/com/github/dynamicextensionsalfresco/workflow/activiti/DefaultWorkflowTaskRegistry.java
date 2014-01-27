package com.github.dynamicextensionsalfresco.workflow.activiti;

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

    private final Map<String, TaskListener> listeners = new ConcurrentHashMap<String, TaskListener>();

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
    public void registerListener(@Nonnull String id, @Nonnull TaskListener taskListener) {
        final TaskListener existing = listeners.put(id, taskListener);
        if (existing != null) {
            throw new IllegalStateException(String.format("overwrite of existing listener using id <%s>", id));
        }
    }

    @Override
    public void unregisterListener(@Nonnull String id) {
        listeners.remove(id);
    }

    @Override
    public TaskListener findListener(@Nonnull String id) {
        return listeners.get(id);
    }
}
