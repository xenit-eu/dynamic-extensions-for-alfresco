package com.github.dynamicextensionsalfresco.quartz;

import com.github.dynamicextensionsalfresco.jobs.ScheduledQuartzJob;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import org.alfresco.repo.lock.JobLockService;
import org.alfresco.schedule.AbstractScheduledLockedJob;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
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
 * Created by jasper on 19/07/17.
 */
public class QuartzJobRegistrar implements ApplicationContextAware, InitializingBean, DisposableBean {
    private Logger logger = LoggerFactory.getLogger(QuartzJobRegistrar.class);

    public final static String BEAN_ID="bean";
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

    public void registerJob(Object bean) throws ParseException, SchedulerException {
        Assert.isInstanceOf(Job.class, bean, "annotated Quartz job classes should implement org.quartz.Job");

        ScheduledQuartzJob annotation = bean.getClass().getAnnotation(ScheduledQuartzJob.class);

        String cron = applicationContext.getBean("global-properties", Properties.class).getProperty(annotation.cronProp(), annotation.cron());
        CronTrigger trigger = new CronTrigger(annotation.name(), annotation.group(), cron);
        JobDetail jobDetail = new JobDetail(annotation.name(), annotation.group(), annotation.cluster() ? ClusterLockedJob.class : GenericQuartzJob.class);

        JobDataMap map = new JobDataMap();
        map.put(BEAN_ID, bean);
        map.put(JOB_LOCK_SERVICE, this.jobLockService);

        jobDetail.setJobDataMap(map);
        scheduler.scheduleJob(jobDetail, trigger);

        registeredJobs.add(annotation);

        logger.debug("scheduled job " + annotation.name() + " from group " + annotation.group() + " using cron " + annotation.cron());
    }

    @Override
    public void destroy() {
        for (ScheduledQuartzJob job : registeredJobs) {
            try {
                scheduler.unscheduleJob(job.name(), job.group());
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