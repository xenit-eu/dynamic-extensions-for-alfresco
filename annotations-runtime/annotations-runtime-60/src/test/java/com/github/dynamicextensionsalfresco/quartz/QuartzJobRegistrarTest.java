package com.github.dynamicextensionsalfresco.quartz;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import com.github.dynamicextensionsalfresco.jobs.ScheduledQuartzJob;
import org.alfresco.repo.lock.JobLockService;
import org.junit.Test;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

public class QuartzJobRegistrarTest {

    @Test
    public void testScheduledJob() throws SchedulerException {
        TestJob job = new TestJob();

        ScheduledQuartzJob annotation = job.getClass().getAnnotation(ScheduledQuartzJob.class);
        assertNotNull("Job not annotated with @ScheduledQuartzJob", annotation);
        Scheduler scheduler = new FakeScheduler();

        QuartzJobRegistrar registrar = new QuartzJobRegistrar();
        registrar.setScheduler(scheduler);
        registrar.registerJob(job);

        scheduler.triggerJob(new JobKey(annotation.name(), annotation.group()));

        assertTrue("scheduled job was not executed", job.isExecuted());
    }

    @Test
    public void testClusteredJob() throws SchedulerException {
        TestJob job = new TestClusteredJob();

        ScheduledQuartzJob annotation = job.getClass().getAnnotation(ScheduledQuartzJob.class);
        assertNotNull("Job not annotated with @ScheduledQuartzJob", annotation);
        Scheduler scheduler = new FakeScheduler();

        QuartzJobRegistrar registrar = new QuartzJobRegistrar();
        registrar.setScheduler(scheduler);
        registrar.setJobLockService(mock(JobLockService.class));

        registrar.registerJob(job);

        scheduler.triggerJob(new JobKey(annotation.name(), annotation.group()));

        assertTrue("scheduled job was not executed", job.isExecuted());
    }


    @ScheduledQuartzJob(cron = "*/15 * * ? * *", name = "test")
    public class TestJob implements Job {

        private boolean executed = false;

        @Override
        public void execute(JobExecutionContext jobExecutionContext) {
            this.executed = true;
        }

        boolean isExecuted() {
            return executed;
        }
    }

    @ScheduledQuartzJob(cron = "*/15 * * ? * *", name = "cluster", cluster = true)
    public class TestClusteredJob extends TestJob {

    }
}