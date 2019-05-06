package com.github.dynamicextensionsalfresco.schedule.quartz;

import org.alfresco.schedule.AbstractScheduledLockedJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class ClusterLockedQuartzJob extends AbstractScheduledLockedJob {

    @Override
    public void executeJob(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        QuartzJobAdaptor.execute(jobExecutionContext);
    }
}
