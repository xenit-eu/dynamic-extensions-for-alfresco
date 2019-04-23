package com.github.dynamicextensionsalfresco.schedule.quartz2;

import java.util.Date;
import org.quartz.Calendar;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerKey;

public class FakeJobExecutionContext implements JobExecutionContext {

    private final Scheduler scheduler;
    private final JobDetail detail;
    private final Job job;

    public FakeJobExecutionContext(Scheduler scheduler, JobDetail detail, Job job) {
        this.scheduler = scheduler;
        this.detail = detail;
        this.job = job;
    }

    @Override
    public Scheduler getScheduler() {
        return this.scheduler;
    }

    @Override
    public Trigger getTrigger() {
        return null;
    }

    @Override
    public Calendar getCalendar() {
        return null;
    }

    @Override
    public boolean isRecovering() {
        return false;
    }

    @Override
    public TriggerKey getRecoveringTriggerKey() throws IllegalStateException {
        return null;
    }

    @Override
    public int getRefireCount() {
        return 0;
    }

    @Override
    public JobDataMap getMergedJobDataMap() {
        return null;
    }

    @Override
    public JobDetail getJobDetail() {
        return this.detail;
    }

    @Override
    public Job getJobInstance() {
        return job;
    }

    @Override
    public Date getFireTime() {
        return null;
    }

    @Override
    public Date getScheduledFireTime() {
        return null;
    }

    @Override
    public Date getPreviousFireTime() {
        return null;
    }

    @Override
    public Date getNextFireTime() {
        return null;
    }

    @Override
    public String getFireInstanceId() {
        return null;
    }

    @Override
    public Object getResult() {
        return null;
    }

    @Override
    public void setResult(Object result) {

    }

    @Override
    public long getJobRunTime() {
        return 0;
    }

    @Override
    public void put(Object key, Object value) {

    }

    @Override
    public Object get(Object key) {
        return null;
    }
}
