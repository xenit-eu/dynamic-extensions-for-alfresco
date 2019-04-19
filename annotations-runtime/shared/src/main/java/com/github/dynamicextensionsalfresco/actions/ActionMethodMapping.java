package com.github.dynamicextensionsalfresco.actions;

import com.github.dynamicextensionsalfresco.actions.annotations.ActionMethod;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import org.alfresco.repo.action.executer.ActionExecuter;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.rule.RuleServiceException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.util.ReflectionUtils;

/**
 * Represents a mapping from {@link ActionExecuter#execute} to an {@link ActionMethod} -annotated method.
 *
 * @author Laurens Fridael
 */
public final class ActionMethodMapping {

    private int nodeRefParameterIndex = -1;
    private int actionParameterIndex = -1;
    private final int parameterCount;
    private final HashMap<String, ParameterMapping> parameterMappingsByName;
    private final Object bean;
    private final Method method;

    public ActionMethodMapping(@NotNull Object bean, @NotNull Method method) {
        if (bean == null) {
            throw new IllegalArgumentException("bean is null");
        }
        if (method == null) {
            throw new IllegalArgumentException("method is null");
        }
        this.bean = bean;
        this.method = method;
        this.parameterMappingsByName = new HashMap<>();
        this.parameterCount = this.method.getParameterTypes().length;
    }

    public final int getNodeRefParameterIndex() {
        return this.nodeRefParameterIndex;
    }

    public final void setNodeRefParameterIndex(int var1) {
        this.nodeRefParameterIndex = var1;
    }

    public final int getActionParameterIndex() {
        return this.actionParameterIndex;
    }

    public final void setActionParameterIndex(int var1) {
        this.actionParameterIndex = var1;
    }

    public final void invokeActionMethod(@NotNull Action action, @Nullable NodeRef nodeRef) {
        if (action == null) {
            throw new IllegalArgumentException("action is null");
        }

        Object[] parameters = new Object[this.parameterCount];
        if (this.nodeRefParameterIndex > -1) {
            parameters[this.nodeRefParameterIndex] = nodeRef;
        }
        if (this.actionParameterIndex > -1) {
            parameters[this.actionParameterIndex] = action;
        }
        for (ParameterMapping parameterMapping : this.parameterMappingsByName.values()) {
            Object value = action.getParameterValue(parameterMapping.getName());
            if (parameterMapping.isMandatory() && value == null) {
                /*
                 * We throw RuleServiceException just as ParameterizedItemAbstractBase does when it encounters a missing
                 * value for a mandatory property.
                 */
                throw new RuleServiceException(
                        "Parameter '" + parameterMapping.getName() + "' is mandatory, but no value was given.");
            }
            /* Single values for a multi-valued property are wrapped in an ArrayList automatically. */
            if (parameterMapping.isMultivalued() && !(value instanceof Collection)) {
                value = Arrays.asList(value);
            }
            parameters[parameterMapping.getIndex()] = value;
        }

        ReflectionUtils.invokeMethod(this.method, this.bean, Arrays.copyOf(parameters, parameters.length));
    }

    public final boolean hasParameter(@NotNull String name) {
        if (name == null) {
            throw new IllegalArgumentException("name is null");
        }

        return this.parameterMappingsByName.containsKey(name);
    }

    public final void addParameterMapping(@NotNull ParameterMapping parameterMapping) {
        if (parameterMapping == null) {
            throw new IllegalArgumentException("parameterMapping is null");
        }

        String name = parameterMapping.getName();
        if (!this.parameterMappingsByName.containsKey(name)) {
            this.parameterMappingsByName.put(name, parameterMapping);
        } else {
            throw new IllegalStateException("Duplicate parameter name '" + name + "'.");
        }
    }
}
