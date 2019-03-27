package com.github.dynamicextensionsalfresco.metrics

import org.alfresco.repo.transaction.AlfrescoTransactionSupport
import org.alfresco.repo.transaction.TransactionListener
import org.slf4j.LoggerFactory
import org.springframework.transaction.support.TransactionSynchronizationManager
import org.springframework.util.StopWatch

/**
 * [Timer] based on Spring's [StopWatch]
 * collects data during a transaction and logs to log4j after commit at TRACE level
 */
public class SpringTimer : Timer {
    val logger = LoggerFactory.getLogger(javaClass)

    val identifier = javaClass.`package`.name

    override fun isEnabled(): Boolean {
        return logger.isTraceEnabled
    }

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
            override fun flush() {}

            override fun beforeCompletion() {}

            override fun beforeCommit(readOnly: Boolean) {}

            override fun afterRollback() {}

            override fun afterCommit() {
                logger.trace(stopWatch.prettyPrint())
            }
        })
    }

    override fun start(label: String) {
        if (isEnabled) {
            with(stopWatch) {
                if (isRunning) {
                    stop()
                }
                start(label)
            }
        }
    }

    override fun stop() {
        if (isEnabled) {
            with(stopWatch) {
                if (isRunning) {
                    stop()
                }
            }
        }
    }
}
