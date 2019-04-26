package com.github.dynamicextensionsalfresco.quartz;

import org.alfresco.schedule.AbstractScheduledLockedJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class ClusterLockedJob extends AbstractScheduledLockedJob {

    @Override
    public void executeJob(JobExecutionContext jobContext) throws JobExecutionException {
        QuartzJobAdaptor.execute(jobContext);
    }
}
