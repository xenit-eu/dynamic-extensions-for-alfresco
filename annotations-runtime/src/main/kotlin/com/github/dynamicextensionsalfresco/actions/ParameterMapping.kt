package com.github.dynamicextensionsalfresco.actions

import org.alfresco.service.cmr.action.ParameterDefinition

/**
 * Represents a mapping of an Action parameter to a parameter of an [ActionMethod]-annotated method.

 * @author Laurens Fridael
 */
class ParameterMapping(parameterDefinition: ParameterDefinition, val index: Int) {

    val name: String

    val isMultivalued: Boolean

    val isMandatory: Boolean

    init {
        this.name = parameterDefinition.name
        this.isMultivalued = parameterDefinition.isMultiValued
        this.isMandatory = parameterDefinition.isMandatory
    }
}
