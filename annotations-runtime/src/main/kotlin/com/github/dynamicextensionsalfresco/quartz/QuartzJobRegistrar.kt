package com.github.dynamicextensionsalfresco.quartz

import com.github.dynamicextensionsalfresco.debug
import com.github.dynamicextensionsalfresco.jobs.ScheduledQuartzJob
import org.quartz.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.BeansException
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component

import java.util.ArrayList

/**
 * Look for beans annotated with @ScheduledJob and register/unregister them with Quartz
 *
 * @author Laurent Van der Linden
 */
public class QuartzJobRegistrar @Autowired constructor(var scheduler: Scheduler) : ApplicationContextAware, InitializingBean, DisposableBean {
    private val logger = LoggerFactory.getLogger(javaClass<QuartzJobRegistrar>())

    private val registeredJobs = ArrayList<ScheduledQuartzJob>()

    private var applicationContext: ApplicationContext? = null

    throws(Exception::class)
    override fun afterPropertiesSet() {
        val scheduledBeans = applicationContext!!.getBeansWithAnnotation(javaClass<ScheduledQuartzJob>())
        for (entry in scheduledBeans.entrySet()) {
            val bean = entry.getValue()
            val annotation = bean.javaClass.getAnnotation(javaClass<ScheduledQuartzJob>())

            try {
                val trigger = CronTrigger(annotation.name, annotation.group, annotation.cron)
                val jobDetail = JobDetail(annotation.name, annotation.group, javaClass<GenericQuartzJob>())
                jobDetail.getJobDataMap().put(GenericQuartzJob.BEAN_ID, bean)
                scheduler.scheduleJob(jobDetail, trigger)

                registeredJobs.add(annotation)

                logger.debug {"scheduled job for bean ${entry.getKey()}" }
            } catch (e: Exception) {
                logger.error("failed to register job ${annotation.name} using cron ${annotation.cron}", e)
            }

        }
    }

    throws(Exception::class)
    override fun destroy() {
        for (job in registeredJobs) {
            try {
                scheduler.unscheduleJob(job.name, job.group)
            } catch (e: SchedulerException) {
                logger.error("failed to cleanup quartz job " + job, e)
            }
        }
    }

    throws(BeansException::class)
    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }
}
