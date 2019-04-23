package com.github.dynamicextensionsalfresco.schedule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.github.dynamicextensionsalfresco.jobs.ScheduledQuartzJob;
import org.junit.Test;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

public class ScheduledTaskRegistrarTest {

    @Test
    public void taskGetsScheduled() throws TaskSchedulerException {
        TestTask task = new TestTask();

        ScheduledTask annotation = task.getClass().getAnnotation(ScheduledTask.class);
        assertNotNull("Job not annotated with @ScheduledTask", annotation);
        TaskScheduler scheduler = mock(TaskScheduler.class);

        ScheduledTaskRegistrar registrar = new ScheduledTaskRegistrar();
        registrar.setScheduler(scheduler);

        registrar.registerTask(task);

        verify(scheduler, times(1))
                .scheduleTask(any(TaskConfiguration.class), any(Task.class));
    }

    @Test
    public void deprecatedQuartzJobGetsScheduled() throws TaskSchedulerException {
        TestQuartzJob job = new TestQuartzJob();

        ScheduledQuartzJob annotation = job.getClass().getAnnotation(ScheduledQuartzJob.class);
        assertNotNull("Job not annotated with @ScheduledQuartzJob", annotation);
        TaskScheduler scheduler = mock(TaskScheduler.class);

        ScheduledTaskRegistrar registrar = new ScheduledTaskRegistrar();
        registrar.setScheduler(scheduler);

        registrar.registerTask(job);

        verify(scheduler, times(1))
                .scheduleTask(any(TaskConfiguration.class), any(Job.class));
    }

    @Test
    public void testSimpleTaskConfig() {
        ScheduledTaskRegistrar registrar = new ScheduledTaskRegistrar();
        TaskConfiguration config = registrar.getTaskConfiguration(new TestTask());

        ScheduledTask annotation = TestTask.class.getAnnotation(ScheduledTask.class);
        assertEquals(config.getName(), annotation.name());
        assertEquals(config.getGroup(), annotation.group());
        assertEquals(config.getCron(), annotation.cron());
        assertEquals(config.isCluster(), annotation.cluster());
    }


    @ScheduledTask(cron = "*/15 * * ? * *", name = "test")
    public class TestTask implements Task {

        private boolean executed = false;

        @Override
        public void execute() {
            this.executed = true;
        }

        boolean isExecuted() {
            return executed;
        }
    }

    @ScheduledQuartzJob(cron = "*/15 * * ? * *", name = "test")
    public class TestQuartzJob implements Job {

        private boolean executed = false;

        @Override
        public void execute(JobExecutionContext jobExecutionContext) {
            this.executed = true;
        }

        boolean isExecuted() {
            return executed;
        }
    }

//    @ScheduledQuartzJob(cron = "*/15 * * ? * *", name = "cluster", cluster = true)
//    public class TestClusteredJob extends TestTask {
//
//    }
}