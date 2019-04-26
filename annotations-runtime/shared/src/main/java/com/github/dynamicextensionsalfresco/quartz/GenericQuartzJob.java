package com.github.dynamicextensionsalfresco.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public final class GenericQuartzJob implements Job {

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
       QuartzJobAdaptor.execute(jobExecutionContext);
    }

}