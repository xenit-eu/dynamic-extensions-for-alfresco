package com.github.dynamicextensionsalfresco.quartz

import org.quartz.Job
import org.quartz.JobExecutionContext
import org.quartz.JobExecutionException

/**
 * Quartz requires a Job class, not an instance, so we use this generic delegate.

 * @author Laurent Van der Linden
 */
public class GenericQuartzJob : Job {

    @Throws(JobExecutionException::class)
    override fun execute(jobExecutionContext: JobExecutionContext) {
        val lockedJob = jobExecutionContext.getJobDetail().getJobDataMap().get(BEAN_ID) as Job
        lockedJob.execute(jobExecutionContext)
    }

    companion object {
        public val BEAN_ID: String = "bean"
    }
}
