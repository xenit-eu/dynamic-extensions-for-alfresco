package com.github.dynamicextensionsalfresco.quartz

import com.github.dynamicextensionsalfresco.debug
import com.github.dynamicextensionsalfresco.jobs.ScheduledQuartzJob
import org.quartz.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.util.Assert
import java.util.*

/**
 * Look for beans annotated with @ScheduledJob and register/unregister them with Quartz
 *
 * @author Laurent Van der Linden
 */
public class QuartzJobRegistrar @Autowired constructor(var scheduler: Scheduler) : ApplicationContextAware, InitializingBean, DisposableBean {
    private val logger = LoggerFactory.getLogger(QuartzJobRegistrar::class.java)

    private val registeredJobs = ArrayList<ScheduledQuartzJob>()

    private var applicationContext: ApplicationContext? = null

    @Throws(Exception::class)
    override fun afterPropertiesSet() {
        val scheduledBeans = applicationContext!!.getBeansWithAnnotation(ScheduledQuartzJob::class.java)
        for (entry in scheduledBeans.entries) {
            val bean = entry.value

            Assert.isInstanceOf(Job::class.java, bean, "annotated Quartz job classes should implement org.quartz.Job")

            val annotation = bean.javaClass.getAnnotation(ScheduledQuartzJob::class.java)

            try {
                val cron = applicationContext!!.getBean("global-properties", Properties::class.java).getProperty(annotation.cronProp, annotation.cron);
                val trigger = CronTrigger(annotation.name, annotation.group, cron)
                val jobDetail = JobDetail(annotation.name, annotation.group, GenericQuartzJob::class.java)
                jobDetail.jobDataMap.put(GenericQuartzJob.BEAN_ID, bean)
                scheduler.scheduleJob(jobDetail, trigger)

                registeredJobs.add(annotation)

                logger.debug { "scheduled job ${annotation.name} from group ${annotation.group} using cron ${annotation.cron}" }
            } catch (e: Exception) {
                logger.error("failed to register job ${annotation.name} using cron ${annotation.cron}", e)
            }

        }
    }

    @Throws(Exception::class)
    override fun destroy() {
        for (job in registeredJobs) {
            try {
                scheduler.unscheduleJob(job.name, job.group)
                logger.debug { "unscheduled job ${job.name} from group ${job.group}" }
            } catch (e: SchedulerException) {
                logger.error("failed to cleanup quartz job " + job, e)
            }
        }
    }

    override fun setApplicationContext(applicationContext: ApplicationContext?) {
        this.applicationContext = applicationContext
    }
}
