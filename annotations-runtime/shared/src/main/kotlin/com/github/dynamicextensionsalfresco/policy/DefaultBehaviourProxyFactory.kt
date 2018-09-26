//package com.github.dynamicextensionsalfresco.policy
//
//import com.github.dynamicextensionsalfresco.debug
//import java.util.ArrayList
//
//import com.github.dynamicextensionsalfresco.metrics.Timer
//import org.alfresco.repo.policy.Behaviour
//import org.slf4j.Logger
//import org.slf4j.LoggerFactory
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.context.ApplicationListener
//import org.springframework.context.event.ContextClosedEvent
//
///**
// * Default [BehaviourProxyFactory] implementation intended for use in a Spring application context. This
// * implementation releases references to [Behaviour]s when the [ContextClosedEvent] occurs.
//
// * @author Laurens Fridael
// */
//public class DefaultBehaviourProxyFactoryOld @Autowired constructor(private val timer: Timer) : BehaviourProxyFactory, ApplicationListener<ContextClosedEvent> {
//
//    private val logger = LoggerFactory.getLogger(javaClass)
//
//    /* State */
//
//    private val behaviourProxies = ArrayList<BehaviourProxy>()
//
//    /* Main operations */
//
//    override fun createBehaviourProxy(behaviour: Behaviour): BehaviourProxy {
//        logger.debug {
//            "Creating BehaviourProxy for ${behaviour.javaClass.name} instance."
//        }
//        val behaviourProxy = BehaviourProxy(behaviour, timer)
//        behaviourProxies.add(behaviourProxy)
//        return behaviourProxy
//    }
//
//    /* Callback operations */
//
//    override fun onApplicationEvent(event: ContextClosedEvent) {
//        releaseBehaviourReferences()
//    }
//
//    /* Utility operations */
//
//    protected fun releaseBehaviourReferences() {
//        for (behaviourProxy in behaviourProxies) {
//            logger.debug { "Releasing reference from BehaviourProxy to $behaviourProxy instance." }
//            behaviourProxy.release()
//        }
//        behaviourProxies.clear()
//    }
//
//}
