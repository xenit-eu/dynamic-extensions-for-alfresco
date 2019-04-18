package com.github.dynamicextensionsalfresco.actions;

import com.github.dynamicextensionsalfresco.actions.annotations.ActionMethod;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a mapping of an Action parameter to a parameter of an {@link ActionMethod}-annotated method.
 *
 * @author Laurens Fridael
 */
public final class ParameterMapping {

    @NotNull
    private final String name;
    private final boolean isMultivalued;
    private final boolean isMandatory;
    private final int index;

    public ParameterMapping(@NotNull ParameterDefinition parameterDefinition, int index) {

        if (parameterDefinition == null) {
            throw new IllegalArgumentException("parameterDefinition is null");
        }

        this.index = index;
        String parameterDefinitionName = parameterDefinition.getName();

        if (parameterDefinitionName == null) {
            throw new IllegalStateException("parameterDefinitionName is null");
        }
        this.name = parameterDefinitionName;
        this.isMultivalued = parameterDefinition.isMultiValued();
        this.isMandatory = parameterDefinition.isMandatory();
    }

    @NotNull
    public final String getName() {
        return this.name;
    }

    public final boolean isMultivalued() {
        return this.isMultivalued;
    }

    public final boolean isMandatory() {
        return this.isMandatory;
    }

    public final int getIndex() {
        return this.index;
    }
}
