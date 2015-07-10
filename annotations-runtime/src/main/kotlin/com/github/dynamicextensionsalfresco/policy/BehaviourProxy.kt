package com.github.dynamicextensionsalfresco.policy

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.concurrent.ConcurrentHashMap

import com.github.dynamicextensionsalfresco.metrics.Timer
import com.github.dynamicextensionsalfresco.metrics.time
import org.alfresco.repo.policy.Behaviour
import org.alfresco.repo.policy.Policy
import org.alfresco.repo.policy.PolicyComponent
import org.alfresco.service.cmr.repository.NodeRef
import org.springframework.util.Assert
import java.lang.reflect.InvocationTargetException

/**
 * Proxy that allows a [Behaviour] to be garbage-collected.
 *
 *
 * This class prevents dangling references to [Behaviour] instances when code is undeployed from an OSGi
 * container, as the [PolicyComponent] interface offers no means of unregistering [Behaviour]s. Dangling
 * references to the [BehaviourProxy] itself will continue to exist throughout the lifetime of the Alfresco
 * process, however. There will be a slight memory leak for every time you redeploy a Dynamic Extension that contains a
 * Behaviour. (Further revisions of this class may add the ability to reattach a Behaviour once a module gets updated.)

 * @author Laurens Fridael
 */
public class BehaviourProxy(private var behaviour: Behaviour, val timer: Timer) : Behaviour by behaviour {

    private val proxiesByPolicyClass = ConcurrentHashMap<Class<*>, ProxyPolicy>()

    suppress("UNCHECKED_CAST")
    override fun <T> getInterface(policy: Class<T>?): T {
        return proxiesByPolicyClass.getOrPut(policy) {
            if (behaviour is NoOpBehaviour) {
                val proxyHandler = ProxyPolicyInvocationHandler(null, behaviour, timer)
                val proxy = Proxy.newProxyInstance(javaClass.getClassLoader(), arrayOf(policy), proxyHandler) as T
                ProxyPolicy(proxy, proxyHandler)
            } else {
                val originalHandler = behaviour.getInterface<T>(policy)
                val proxyHandler = ProxyPolicyInvocationHandler(originalHandler, behaviour, timer)
                val proxy = Proxy.newProxyInstance(javaClass.getClassLoader(), arrayOf(policy), proxyHandler) as T
                ProxyPolicy(proxy, proxyHandler)
            }
        }.proxy as T
    }

    /**
     * Clears the reference to the original [Behaviour] and clears the target references for the
     * [ProxyPolicyComponentInvocationHandler]s.
     */
    synchronized public fun release() {
        behaviour = NoOpBehaviour(behaviour.getNotificationFrequency(), behaviour.isEnabled())
        for (proxyPolicy in proxiesByPolicyClass.values()) {
            proxyPolicy.handler.release()
        }
    }

    private class ProxyPolicyInvocationHandler(private var target: Any?, private var behaviour: Behaviour?, val timer: Timer) : InvocationHandler {

        override fun invoke(proxy: Any, method: Method, args: Array<Any>?): Any? {
            if (method.getDeclaringClass().isAssignableFrom(javaClass<Any>())) {
                // Direct Object methods to ourselves.
                return method.invoke(this, *args)
            } else if (javaClass<Policy>().isAssignableFrom(method.getDeclaringClass())) {
                /* Policy interface operations always return void. */
                if (behaviour != null) {
                    try {
                        timer.time( {
                            behaviour.toString() + " " + args?.filterIsInstance(javaClass<NodeRef>())?.joinToString(",")
                        } , {method.invoke(target, *args)})
                    } catch(e: InvocationTargetException) {
                        throw e.getTargetException()
                    }
                }
                return null
            } else {
                /* We should never get to this point. */
                throw AssertionError("Cannot handle methods from " + method.getDeclaringClass())
            }
        }

        fun release() {
            target = null
            behaviour = null
        }
    }

    override fun toString(): String {
        return behaviour.toString()
    }

    private class ProxyPolicy(val proxy: Any, val handler: ProxyPolicyInvocationHandler)
}
