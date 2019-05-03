package com.github.dynamicextensionsalfresco.schedule.quartz;

import com.github.dynamicextensionsalfresco.schedule.TaskConfiguration;
import com.github.dynamicextensionsalfresco.schedule.TaskRegistration;
import com.github.dynamicextensionsalfresco.schedule.TaskSchedulerException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

public class QuartzTaskRegistration implements TaskRegistration {

    private final Scheduler scheduler;
    private final String name;
    private final String group;

    public QuartzTaskRegistration(Scheduler scheduler, String name, String group) {
        this.scheduler = scheduler;
        this.name = name;
        this.group = group;
    }

    public QuartzTaskRegistration(Scheduler scheduler, TaskConfiguration config) {
        this(scheduler, config.getName(), config.getGroup());
    }


    @Override
    public void unregister() throws TaskSchedulerException {
        try {
            scheduler.unscheduleJob(this.name, this.group);
        } catch (org.quartz.SchedulerException e) {
            throw new TaskSchedulerException(e);
        }
    }

    @Override
    public void trigger() throws TaskSchedulerException {
        try {
            this.scheduler.triggerJob(this.name, this.group);
        } catch (SchedulerException e) {
            throw new TaskSchedulerException(e);
        }
    }
}
