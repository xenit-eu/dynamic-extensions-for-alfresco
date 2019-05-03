package com.github.dynamicextensionsalfresco.schedule.quartz2;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public final class GenericQuartzJob implements org.quartz.Job {

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
       QuartzJobAdaptor.execute(jobExecutionContext);
    }

}