package com.github.dynamicextensionsalfresco.workflow.activiti

import com.github.dynamicextensionsalfresco.debug
import com.github.dynamicextensionsalfresco.warn
import org.activiti.engine.delegate.ExecutionListener
import org.activiti.engine.delegate.JavaDelegate
import org.activiti.engine.delegate.TaskListener
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware

/**
 * Detects workflow tasks from Spring beans implementing the [DelegateTask] or [TaskListener] interface.
 *
 * Still links components to the [WorkflowTaskRegistry] for backwards compatibility, but delegate expressions are now
 * the recommended way of referencing components from bpm files.

 * @author Laurent Van der Linden
 */
public open class WorkflowTaskRegistrar(val activitiBeanRegistry: MutableMap<String, Any>, val workflowTaskRegistry: WorkflowTaskRegistry)
        : InitializingBean, ApplicationContextAware, DisposableBean {
    private val logger = LoggerFactory.getLogger(WorkflowTaskRegistrar::class.java)

    private lateinit var applicationContext: ApplicationContext

    private fun workflowDelegateTypes() = listOf(
            JavaDelegate::class.java, TaskListener::class.java, ExecutionListener::class.java
    )

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }

    override fun afterPropertiesSet() {
        for (type in workflowDelegateTypes()) {
            val delegates = applicationContext.getBeansOfType(type)
            for ((name,bean) in delegates) {
                val existing = activitiBeanRegistry.put(name, bean)
                if (existing != null) {
                    logger.warn { "replaced existing ${existing.javaClass} with name $name"}
                }
                logger.debug { "registered Bundle component $name (type: $type) -> $bean" }

                // legacy
                when (bean) {
                    is JavaDelegate -> workflowTaskRegistry.registerDelegate(name, bean)
                    is TaskListener -> workflowTaskRegistry.registerTaskListener(name, bean)
                    is ExecutionListener -> workflowTaskRegistry.registerExecutionListener(name, bean)
                }
            }
        }
    }

    override fun destroy() {
        for (type in workflowDelegateTypes()) {
            val delegates = applicationContext.getBeansOfType(type)
            for ((name, bean) in delegates) {
                activitiBeanRegistry.remove(name)
                logger.debug { "unregistered Bundle component $name (type: $type) -> $bean" }

                // legacy
                when (bean) {
                    is JavaDelegate -> workflowTaskRegistry.unregisterDelegate(name)
                    is TaskListener -> workflowTaskRegistry.unregisterTaskListener(name)
                    is ExecutionListener -> workflowTaskRegistry.unregisterExecutionListener(name)
                }
            }
        }
    }
}
