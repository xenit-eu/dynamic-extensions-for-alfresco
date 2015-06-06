package com.github.dynamicextensionsalfresco.policy

import org.alfresco.repo.policy.Behaviour
import org.alfresco.repo.policy.JavaBehaviour
import java.lang.reflect.Method

/**
 * Simplify toString to method signature
 *
 * @author Laurent Van der Linden
 */
class DescriptiveJavaBehaviour(instance: Any?, private val methodReference: Method, frequency: Behaviour.NotificationFrequency?)
        : JavaBehaviour(instance, methodReference.getName(), frequency) {
    override fun toString(): String? {
        return methodReference.getDeclaringClass().getName() + "." + methodReference.getName()
    }
}