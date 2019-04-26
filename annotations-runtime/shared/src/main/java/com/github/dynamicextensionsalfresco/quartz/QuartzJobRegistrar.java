package com.github.dynamicextensionsalfresco.quartz;

import com.github.dynamicextensionsalfresco.jobs.ScheduledQuartzJob;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import org.alfresco.repo.lock.JobLockService;
import org.alfresco.schedule.AbstractScheduledLockedJob;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

/**
 * This class is overwritten for older versions of Alfresco. When making changes here, make sure you also take a look to
 * the implementation for older Alfresco versions.
 */
public class QuartzJobRegistrar implements ApplicationContextAware, InitializingBean, DisposableBean {


    private Logger logger = LoggerFactory.getLogger(QuartzJobRegistrar.class);

    public final static String BEAN_ID = "bean";
    public static final String JOB_LOCK_SERVICE = "jobLockService";

    private ArrayList<ScheduledQuartzJob> registeredJobs = new ArrayList<ScheduledQuartzJob>();

    @Autowired
    protected Scheduler scheduler;

    @Autowired
    protected JobLockService jobLockService;

    @Autowired
    @Qualifier("global-properties")
    protected Properties globalProperties = new Properties();

    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() {
        this.registerScheduledQuartzJobAnnotatedBeans();
    }

    public void registerScheduledQuartzJobAnnotatedBeans() {
        Map<String, Object> scheduledBeans = applicationContext.getBeansWithAnnotation(ScheduledQuartzJob.class);
        for (Map.Entry entry : scheduledBeans.entrySet()) {
            Object bean = entry.getValue();
            try {
                this.registerJob(bean);
            } catch (Exception e) {
                logger.error("Failed to register job: ", e);
            }
        }
    }

    public void registerJob(Object bean) throws SchedulerException {
        Assert.isInstanceOf(Job.class, bean, "annotated Quartz job classes should implement org.quartz.Job");

        ScheduledQuartzJob annotation = bean.getClass().getAnnotation(ScheduledQuartzJob.class);

        String cron = this.globalProperties.getProperty(annotation.cronProp(), annotation.cron());
        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(annotation.name(), annotation.group())
                .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                .build();
        JobDataMap map = new JobDataMap();
        map.put(BEAN_ID, bean);
        map.put(JOB_LOCK_SERVICE, this.jobLockService);
        map.put("name", trigger.getKey().toString());

        JobDetail jobDetail = JobBuilder
                .newJob(annotation.cluster() ? ClusterLockedJob.class : GenericQuartzJob.class)
                .withIdentity(annotation.name(), annotation.group())
                .usingJobData(map)
                .build();
        scheduler.scheduleJob(jobDetail, trigger);

        registeredJobs.add(annotation);

        logger.debug("scheduled job " + annotation.name() + " from group " + annotation.group() + " using cron "
                + annotation.cron());

    }

    @Override
    public void destroy() throws SchedulerException {
        for (ScheduledQuartzJob job : registeredJobs) {
            try {
                scheduler.unscheduleJob(TriggerKey.triggerKey(job.name(), job.group()));
                logger.debug("unscheduled job " + job.name() + " from group " + job.group());
            } catch (SchedulerException e) {
                logger.error("failed to cleanup quartz job " + job, e);
            }
        }
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void setJobLockService(JobLockService jobLockService) {
        this.jobLockService = jobLockService;
    }

}