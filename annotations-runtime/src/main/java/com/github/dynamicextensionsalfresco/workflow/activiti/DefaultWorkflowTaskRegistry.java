package com.github.dynamicextensionsalfresco.workflow.activiti;

import org.activiti.engine.delegate.JavaDelegate;

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

    private final Map<String, JavaDelegate> tasks = new ConcurrentHashMap<String, JavaDelegate>();

    @Override
    public void registerTask(@Nonnull String id, @Nonnull JavaDelegate delegate) {
        final JavaDelegate existing = tasks.put(id, delegate);
        if (existing != null) {
            throw new IllegalStateException(String.format("overwrite of existing task using id <%s>", id));
        }
    }

    @Override
    public void unRegisterTask(@Nonnull String id) {
        tasks.remove(id);
    }

    @Override
    public JavaDelegate findDelegate(@Nonnull String id) {
        return tasks.get(id);
    }
}
