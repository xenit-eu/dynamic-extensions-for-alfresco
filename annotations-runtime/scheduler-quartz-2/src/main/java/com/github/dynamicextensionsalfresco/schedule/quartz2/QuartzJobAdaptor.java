package com.github.dynamicextensionsalfresco.schedule.quartz2;

import com.github.dynamicextensionsalfresco.schedule.Task;
import java.util.Map;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class QuartzJobAdaptor {

    static void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDetail jobDetail = jobExecutionContext.getJobDetail();
        Map jobDataMap = jobDetail.getJobDataMap();

        Object obj = jobDataMap.get(Quartz2TaskScheduler.BEAN_ID);
        if (obj == null) {
            throw new JobExecutionException(String.format("Job not found in %s", JobDataMap.class.getSimpleName()));
        }

        if (Task.class.isAssignableFrom(obj.getClass())) {
            Task task = (Task) obj;
            task.execute();
        } else if (Job.class.isAssignableFrom(obj.getClass())) {
            Job lockedJob = (Job) obj;
            lockedJob.execute(jobExecutionContext);
        } else {
            throw new JobExecutionException("Unexpected type: " + obj.getClass());
        }
    }
}
