package com.github.dynamicextensionsalfresco.schedule.quartz;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.quartz.Calendar;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;
import org.quartz.SchedulerListener;
import org.quartz.SchedulerMetaData;
import org.quartz.Trigger;
import org.quartz.TriggerListener;
import org.quartz.UnableToInterruptJobException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.quartz.utils.Key;

public class FakeQuartzScheduler implements Scheduler {

    private Map<Key, SchedulerJobData> storage = new HashMap<>();

    @Override
    public Date scheduleJob(JobDetail detail, Trigger trigger) {
        storage.put(trigger.getKey(), new SchedulerJobData(detail, trigger));
        return new Date();
    }

    @Override
    public void triggerJob(String name, String group) throws SchedulerException {

        try {
            SchedulerJobData data = storage.get(new Key(name, group));
            JobDetail detail = data.getDetail();
            if (detail == null) {
                return;
            }

            Job job = (Job) detail.getJobClass().newInstance();
            TriggerFiredBundle bndle = new TriggerFiredBundle(detail, data.getTrigger(), null,
                    false, new Date(), null, null, null);
            JobExecutionContext context = new JobExecutionContext(this, bndle, job);

            job.execute(context);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new SchedulerException(e);
        }

    }

    @Override
    public String getSchedulerName() throws SchedulerException {
        return null;
    }

    @Override
    public String getSchedulerInstanceId() throws SchedulerException {
        return null;
    }

    @Override
    public SchedulerContext getContext() throws SchedulerException {
        return null;
    }

    @Override
    public void start() throws SchedulerException {

    }

    @Override
    public void startDelayed(int i) throws SchedulerException {

    }

    @Override
    public boolean isStarted() throws SchedulerException {
        return false;
    }

    @Override
    public void standby() throws SchedulerException {

    }

    @Override
    public boolean isInStandbyMode() throws SchedulerException {
        return false;
    }

    @Override
    public void shutdown() throws SchedulerException {

    }

    @Override
    public void shutdown(boolean b) throws SchedulerException {

    }

    @Override
    public boolean isShutdown() throws SchedulerException {
        return false;
    }

    @Override
    public SchedulerMetaData getMetaData() throws SchedulerException {
        return null;
    }

    @Override
    public List getCurrentlyExecutingJobs() throws SchedulerException {
        return null;
    }

    @Override
    public void setJobFactory(JobFactory jobFactory) throws SchedulerException {

    }


    @Override
    public Date scheduleJob(Trigger trigger) throws SchedulerException {
        return null;
    }

    @Override
    public boolean unscheduleJob(String s, String s1) throws SchedulerException {
        return false;
    }

    @Override
    public Date rescheduleJob(String s, String s1, Trigger trigger) throws SchedulerException {
        return null;
    }

    @Override
    public void addJob(JobDetail jobDetail, boolean b) throws SchedulerException {

    }

    @Override
    public boolean deleteJob(String s, String s1) throws SchedulerException {
        return false;
    }

    @Override
    public void triggerJobWithVolatileTrigger(String s, String s1) throws SchedulerException {

    }

    @Override
    public void triggerJob(String s, String s1, JobDataMap jobDataMap) throws SchedulerException {

    }

    @Override
    public void triggerJobWithVolatileTrigger(String s, String s1, JobDataMap jobDataMap) throws SchedulerException {

    }

    @Override
    public void pauseJob(String s, String s1) throws SchedulerException {

    }

    @Override
    public void pauseJobGroup(String s) throws SchedulerException {

    }

    @Override
    public void pauseTrigger(String s, String s1) throws SchedulerException {

    }

    @Override
    public void pauseTriggerGroup(String s) throws SchedulerException {

    }

    @Override
    public void resumeJob(String s, String s1) throws SchedulerException {

    }

    @Override
    public void resumeJobGroup(String s) throws SchedulerException {

    }

    @Override
    public void resumeTrigger(String s, String s1) throws SchedulerException {

    }

    @Override
    public void resumeTriggerGroup(String s) throws SchedulerException {

    }

    @Override
    public void pauseAll() throws SchedulerException {

    }

    @Override
    public void resumeAll() throws SchedulerException {

    }

    @Override
    public String[] getJobGroupNames() throws SchedulerException {
        return new String[0];
    }

    @Override
    public String[] getJobNames(String s) throws SchedulerException {
        return new String[0];
    }

    @Override
    public Trigger[] getTriggersOfJob(String s, String s1) throws SchedulerException {
        return new Trigger[0];
    }

    @Override
    public String[] getTriggerGroupNames() throws SchedulerException {
        return new String[0];
    }

    @Override
    public String[] getTriggerNames(String s) throws SchedulerException {
        return new String[0];
    }

    @Override
    public Set getPausedTriggerGroups() throws SchedulerException {
        return null;
    }

    @Override
    public JobDetail getJobDetail(String s, String s1) throws SchedulerException {
        return null;
    }

    @Override
    public Trigger getTrigger(String s, String s1) throws SchedulerException {
        return null;
    }

    @Override
    public int getTriggerState(String s, String s1) throws SchedulerException {
        return 0;
    }

    @Override
    public void addCalendar(String s, Calendar calendar, boolean b, boolean b1) throws SchedulerException {

    }

    @Override
    public boolean deleteCalendar(String s) throws SchedulerException {
        return false;
    }

    @Override
    public Calendar getCalendar(String s) throws SchedulerException {
        return null;
    }

    @Override
    public String[] getCalendarNames() throws SchedulerException {
        return new String[0];
    }

    @Override
    public boolean interrupt(String s, String s1) throws UnableToInterruptJobException {
        return false;
    }

    @Override
    public void addGlobalJobListener(JobListener jobListener) throws SchedulerException {

    }

    @Override
    public void addJobListener(JobListener jobListener) throws SchedulerException {

    }

    @Override
    public boolean removeGlobalJobListener(String s) throws SchedulerException {
        return false;
    }

    @Override
    public boolean removeJobListener(String s) throws SchedulerException {
        return false;
    }

    @Override
    public List getGlobalJobListeners() throws SchedulerException {
        return null;
    }

    @Override
    public Set getJobListenerNames() throws SchedulerException {
        return null;
    }

    @Override
    public JobListener getGlobalJobListener(String s) throws SchedulerException {
        return null;
    }

    @Override
    public JobListener getJobListener(String s) throws SchedulerException {
        return null;
    }

    @Override
    public void addGlobalTriggerListener(TriggerListener triggerListener) throws SchedulerException {

    }

    @Override
    public void addTriggerListener(TriggerListener triggerListener) throws SchedulerException {

    }

    @Override
    public boolean removeGlobalTriggerListener(String s) throws SchedulerException {
        return false;
    }

    @Override
    public boolean removeTriggerListener(String s) throws SchedulerException {
        return false;
    }

    @Override
    public List getGlobalTriggerListeners() throws SchedulerException {
        return null;
    }

    @Override
    public Set getTriggerListenerNames() throws SchedulerException {
        return null;
    }

    @Override
    public TriggerListener getGlobalTriggerListener(String s) throws SchedulerException {
        return null;
    }

    @Override
    public TriggerListener getTriggerListener(String s) throws SchedulerException {
        return null;
    }

    @Override
    public void addSchedulerListener(SchedulerListener schedulerListener) throws SchedulerException {

    }

    @Override
    public boolean removeSchedulerListener(SchedulerListener schedulerListener) throws SchedulerException {
        return false;
    }

    @Override
    public List getSchedulerListeners() throws SchedulerException {
        return null;
    }

    private class SchedulerJobData {

        private final JobDetail detail;
        private final Trigger trigger;

        private SchedulerJobData(JobDetail detail, Trigger trigger) {
            this.detail = detail;
            this.trigger = trigger;
        }

        public JobDetail getDetail() {
            return detail;
        }

        public Trigger getTrigger() {
            return trigger;
        }
    }
}
