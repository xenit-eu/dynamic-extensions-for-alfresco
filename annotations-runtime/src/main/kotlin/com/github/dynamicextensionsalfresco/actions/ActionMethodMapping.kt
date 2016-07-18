package com.github.dynamicextensionsalfresco.actions

import org.alfresco.service.cmr.action.Action
import org.alfresco.service.cmr.repository.NodeRef
import org.alfresco.service.cmr.rule.RuleServiceException
import org.springframework.util.ReflectionUtils
import java.lang.reflect.Method
import java.util.*

/**
 * Represents a mapping from [ActionExecuter.execute] to an [ActionMethod] -annotated
 * method.

 * @author Laurens Fridael
 */
class ActionMethodMapping(private val bean: Any, private val method: Method) {

    var nodeRefParameterIndex: Int = -1

    var actionParameterIndex: Int = -1

    private val parameterCount: Int

    private val parameterMappingsByName = HashMap<String, ParameterMapping>()

    init {
        this.parameterCount = method.parameterTypes.size
    }

    fun invokeActionMethod(action: Action, nodeRef: NodeRef?) {
        val parameters = arrayOfNulls<Any>(parameterCount)
        if (nodeRefParameterIndex > -1) {
            parameters[nodeRefParameterIndex] = nodeRef
        }
        if (actionParameterIndex > -1) {
            parameters[actionParameterIndex] = action
        }
        for ((key, parameterMapping) in parameterMappingsByName) {
            var value = action.getParameterValue(parameterMapping.name)
            if (parameterMapping.isMandatory && value == null) {
                /*
                 * We throw RuleServiceException just as ParameterizedItemAbstractBase does when it encounters a missing
                 * value for a mandatory property.
                 */
                throw RuleServiceException("Parameter '${parameterMapping.name}' is mandatory, but no value was given.")
            }
            /* Single values for a multi-valued property are wrapped in an ArrayList automatically. */
            if (parameterMapping.isMultivalued && (value is Collection<*>) == false) {
                value = arrayListOf(value)
            }
            parameters[parameterMapping.index] = value
        }

        ReflectionUtils.invokeMethod(method, bean, *parameters)
    }

    fun hasParameter(name: String): Boolean {
        return parameterMappingsByName.containsKey(name)
    }

    fun addParameterMapping(parameterMapping: ParameterMapping) {
        val name = parameterMapping.name
        if (parameterMappingsByName.containsKey(name) == false) {
            parameterMappingsByName.put(name, parameterMapping)
        } else {
            throw IllegalStateException("Duplicate parameter name '$name'.")
        }
    }
}
