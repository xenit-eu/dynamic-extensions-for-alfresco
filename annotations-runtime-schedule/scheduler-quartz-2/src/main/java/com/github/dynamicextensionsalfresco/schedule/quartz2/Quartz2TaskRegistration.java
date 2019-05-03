package com.github.dynamicextensionsalfresco.schedule.quartz2;

import com.github.dynamicextensionsalfresco.schedule.TaskConfiguration;
import com.github.dynamicextensionsalfresco.schedule.TaskRegistration;
import com.github.dynamicextensionsalfresco.schedule.TaskSchedulerException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;

public class Quartz2TaskRegistration implements TaskRegistration {

    private final Scheduler scheduler;
    private final String name;
    private final String group;


    public Quartz2TaskRegistration(Scheduler scheduler, TaskConfiguration config) {
        this(scheduler, config.getName(), config.getGroup());
    }

    public Quartz2TaskRegistration(Scheduler scheduler, String name, String group) {
        this.scheduler = scheduler;
        this.name = name;
        this.group = group;
    }

    @Override
    public void unregister() throws TaskSchedulerException {
        try {
            scheduler.unscheduleJob(TriggerKey.triggerKey(name, group));
        } catch (org.quartz.SchedulerException e) {
            throw new TaskSchedulerException(e);
        }
    }

    @Override
    public void trigger() throws TaskSchedulerException {
        try {
            this.scheduler.triggerJob(JobKey.jobKey(this.name, this.group));
        } catch (SchedulerException e) {
            throw new TaskSchedulerException(e);
        }
    }
}
