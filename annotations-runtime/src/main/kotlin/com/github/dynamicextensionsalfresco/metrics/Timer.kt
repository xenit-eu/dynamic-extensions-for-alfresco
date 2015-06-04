package com.github.dynamicextensionsalfresco.metrics

import org.alfresco.repo.transaction.AlfrescoTransactionSupport
import org.alfresco.repo.transaction.TransactionListenerAdapter
import org.alfresco.service.transaction.TransactionService
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
        AlfrescoTransactionSupport.bindListener(object : TransactionListenerAdapter() {
            override fun afterCommit() {
                logger.trace(stopWatch.prettyPrint())
            }
        })
    }

    inline fun time(label: String, args: Array<Any>?, operation: () -> Unit) {
        start(label, args)
        try {
            operation()
        } finally {
            stop()
        }
    }

    fun start(label: String, args: Array<Any>?) {
        if (logger.isTraceEnabled()) {
            var stopWatch = stopWatch
            stopWatch.start(label + if (args != null) " - " + Arrays.toString(args) else "")
        }
    }

    fun stop() {
        if (logger.isTraceEnabled()) {
            stopWatch.stop()
        }
    }

    companion object {
        val instance = Timer()
    }
}
