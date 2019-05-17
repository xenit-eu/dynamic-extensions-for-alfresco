package com.github.dynamicextensionsalfresco.schedule.quartz;


import com.github.dynamicextensionsalfresco.schedule.Task;
import com.github.dynamicextensionsalfresco.schedule.TaskConfiguration;
import com.github.dynamicextensionsalfresco.schedule.TaskRegistration;
import com.github.dynamicextensionsalfresco.schedule.TaskScheduler;
import com.github.dynamicextensionsalfresco.schedule.TaskSchedulerException;
import java.text.ParseException;
import org.alfresco.repo.lock.JobLockService;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Pluggable TaskScheduler adaptor for Quartz 1 used in Alfresco 5.x
 */
public class QuartzTaskScheduler implements TaskScheduler {

    private final static Logger log = LoggerFactory.getLogger(QuartzTaskScheduler.class);

    static final Object BEAN_ID = "bean";
    static final String JOB_LOCK_SERVICE = "jobLockService";

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private JobLockService jobLockService;

    @Override
    public TaskRegistration scheduleTask(TaskConfiguration config, Object bean) throws TaskSchedulerException {
        if (config == null) {
            throw new IllegalArgumentException("config is null");
        }
        if (bean == null) {
            throw new IllegalArgumentException("bean is null");
        }

        if (this.scheduler == null) {
            throw new IllegalStateException("schedule is null");
        }

        try {
            // Argument 'bean' should implement either 'Task' or 'org.quartz.Job'
            if (Task.class.isAssignableFrom(bean.getClass())) {
                // this is the expected type, carry on
            } else if (Job.class.isAssignableFrom(bean.getClass())) {
                log.warn("[DEPRECATION] '{}' Implementing {} directly is deprecated, please implement {}",
                        bean.getClass().getCanonicalName(), Job.class.getName(), Task.class.getName());
            } else {
                throw new IllegalArgumentException(String.format("argument 'bean' does not implement interface " +
                        Task.class.getName()));
            }

            CronTrigger trigger = new CronTrigger(config.getName(), config.getGroup(), config.getCron());
            JobDetail jobDetail = new JobDetail(config.getName(), config.getGroup(), this.getJobClass(config));

            JobDataMap map = new JobDataMap();
            map.put(BEAN_ID, bean);
            map.put(JOB_LOCK_SERVICE, this.jobLockService);
            map.put("name", trigger.getKey().toString());

            jobDetail.setJobDataMap(map);

            scheduler.scheduleJob(jobDetail, trigger);

            return new QuartzTaskRegistration(scheduler, config);


        } catch (ParseException | org.quartz.SchedulerException e) {
            throw new TaskSchedulerException(e);
        }
    }

    private Class getJobClass(TaskConfiguration config) {
        return config.isCluster()
                ? ClusterLockedQuartzJob.class
                : GenericQuartzJob.class;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void setJobLockService(JobLockService jobLockService) {
        this.jobLockService = jobLockService;
    }
}
