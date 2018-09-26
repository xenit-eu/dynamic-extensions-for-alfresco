package com.github.dynamicextensionsalfresco.quartz;

import java.util.Map;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public final class GenericQuartzJob implements Job {

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDetail jobDetail = jobExecutionContext.getJobDetail();
        Map jobDataMap = jobDetail.getJobDataMap();
        Object obj = jobDataMap.get(QuartzJobRegistrar.BEAN_ID);
        Job lockedJob = (Job)obj;
        lockedJob.execute(jobExecutionContext);
    }

}