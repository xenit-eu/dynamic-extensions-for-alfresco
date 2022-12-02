package com.github.dynamicextensionsalfresco.metrics;

import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
import org.alfresco.repo.transaction.TransactionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StopWatch;

import javax.validation.constraints.NotNull;

/**
 * {@link Timer} based on Spring's {@link StopWatch} collects data during a transaction and logs to log4j after commit
 * at TRACE level
 */
public final class SpringTimer implements Timer {

    private final Logger logger = LoggerFactory.getLogger(SpringTimer.class);

    private final String identifier = SpringTimer.class.getPackage().getName();

    @Override
    public boolean isEnabled() {
        return logger.isTraceEnabled();
    }

    @Override
    public void start(String label) {
        if (isEnabled()) {
            StopWatch stopWatch = getStopWatch();
            if (stopWatch.isRunning()) {
                stopWatch.stop();
            }
            stopWatch.start(label);
        }
    }

    @Override
    public void stop() {
        if (isEnabled()) {
            StopWatch stopWatch = getStopWatch();
            if (stopWatch.isRunning()) {
                stopWatch.stop();
            }
        }
    }

    @NotNull
    private StopWatch getStopWatch() {
        Object stopWatch = TransactionSynchronizationManager.getResource(identifier);
        if (!(stopWatch instanceof StopWatch)) {
            stopWatch = null;
        }

        if (stopWatch == null) {
            stopWatch = new StopWatch(identifier);
            TransactionSynchronizationManager.bindResource(identifier, stopWatch);

            registerTxListener();
        }
        return (StopWatch) stopWatch;
    }

    private void registerTxListener() {
        AlfrescoTransactionSupport.bindListener(new TransactionListener() {
            @Override
            public void flush() {
            }

            @Override
            public void beforeCommit(boolean readOnly) {
            }

            @Override
            public void beforeCompletion() {
            }

            @Override
            public void afterCommit() {
                logger.trace(getStopWatch().prettyPrint());
            }

            @Override
            public void afterRollback() {
            }
        });
    }
}