package com.github.dynamicextensionsalfresco.metrics

import org.alfresco.repo.policy.JavaBehaviour
import org.alfresco.repo.transaction.AlfrescoTransactionSupport
import org.alfresco.repo.transaction.TransactionListenerAdapter
import org.alfresco.service.cmr.repository.NodeRef
import org.alfresco.service.transaction.TransactionService
import org.alfresco.util.transaction.TransactionListener
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionSynchronizationManager
import org.springframework.util.StopWatch
import java.lang.reflect.InvocationTargetException
import java.util.*

/**
 * register timing information during a transaction and report after commit
 *
 * @author Laurent Van der Linden
 */
public class Timer {
    val logger = LoggerFactory.getLogger(javaClass)

    val identifier = javaClass.getPackage().getName()

    val enabled: Boolean
        get() = logger.isTraceEnabled()

    private val stopWatch: StopWatch
        get() {
            var stopWatch = TransactionSynchronizationManager.getResource(identifier) as? StopWatch
            if (stopWatch == null) {
                stopWatch = StopWatch(identifier)
                TransactionSynchronizationManager.bindResource(identifier, stopWatch)

                registerTxListener()
            }
            return stopWatch
        }

    private fun registerTxListener() {
        AlfrescoTransactionSupport.bindListener(object : TransactionListener {
            override fun beforeCompletion() {}

            override fun beforeCommit(readOnly: Boolean) {}

            override fun afterRollback() {}

            override fun afterCommit() {
                logger.trace(stopWatch.prettyPrint())
            }
        })
    }

    inline fun time(labelProvider: () -> String, operation: () -> Unit) {
        if (enabled) {
            start(labelProvider.invoke())
            try {
                operation()
            } finally {
                stop()
            }
        } else {
            operation()
        }
    }

    fun start(label: String) {
        if (enabled) {
            var stopWatch = stopWatch
            stopWatch.start(label)
        }
    }

    fun stop() {
        if (enabled) {
            stopWatch.stop()
        }
    }

    companion object {
        val instance = Timer()
    }
}
