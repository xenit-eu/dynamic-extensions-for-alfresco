package com.github.dynamicextensionsalfresco.quartz;

import com.github.dynamicextensionsalfresco.jobs.ScheduledQuartzJob;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
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
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

/**
 * Created by jasper on 19/07/17.
 */
public class QuartzJobRegistrar implements ApplicationContextAware, InitializingBean, DisposableBean {
    private Logger logger = LoggerFactory.getLogger(QuartzJobRegistrar.class);

    public final static String BEAN_ID="bean";

    @Autowired
    protected Scheduler scheduler;
    private ArrayList<ScheduledQuartzJob> registeredJobs = new ArrayList<ScheduledQuartzJob>();

    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() throws ParseException, SchedulerException {
        Map<String, Object> scheduledBeans = applicationContext.getBeansWithAnnotation(ScheduledQuartzJob.class);
        for (Map.Entry entry : scheduledBeans.entrySet()) {
            Object bean = entry.getValue();
            Assert.isInstanceOf(Job.class, bean, "annotated Quartz job classes should implement org.quartz.Job");

            ScheduledQuartzJob annotation = bean.getClass().getAnnotation(ScheduledQuartzJob.class);

            try {
                String cron = applicationContext.getBean("global-properties", Properties.class).getProperty(annotation.cronProp(), annotation.cron());
                CronTrigger trigger =TriggerBuilder.newTrigger()
                        .withIdentity(annotation.name(), annotation.group())
                        .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                        .build();
                JobDataMap map = new JobDataMap();
                map.put(BEAN_ID, bean);
                JobDetail jobDetail = JobBuilder.newJob(annotation.cluster() ? AbstractScheduledLockedJob.class : GenericQuartzJob.class)
                        .withIdentity(annotation.name(), annotation.group())
                        .usingJobData(map)
                        .build();
                scheduler.scheduleJob(jobDetail, trigger);

                registeredJobs.add(annotation);

                logger.debug("scheduled job " + annotation.name() + " from group " + annotation.group() + " using cron " + annotation.cron());
            } catch (Exception e) {
                logger.error("failed to register job " + annotation.name() + " using cron " + annotation.group(), e);
            }
        }
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

}