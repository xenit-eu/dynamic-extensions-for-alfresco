package com.github.dynamicextensionsalfresco.schedule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.dynamicextensionsalfresco.jobs.ScheduledQuartzJob;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.context.ApplicationContext;

public class ScheduledTaskRegistrarTest {

    private ScheduledTaskRegistrar registrar;
    private ApplicationContext applicationContext;

    @Before
    public void setup() {
        applicationContext = mock(ApplicationContext.class);

        registrar = new ScheduledTaskRegistrar();
        registrar.setApplicationContext(applicationContext);

    }

    @Test
    public void taskGetsScheduled() throws TaskSchedulerException {
        TestTask task = new TestTask();
        when(applicationContext.getBeansWithAnnotation(ScheduledTask.class))
                .thenReturn(Collections.singletonMap("scheduled-task", task));

        ScheduledTask annotation = task.getClass().getAnnotation(ScheduledTask.class);
        assertNotNull("Job not annotated with @ScheduledTask", annotation);
        TaskScheduler scheduler = mock(TaskScheduler.class);

        registrar.setScheduler(scheduler);

        registrar.afterPropertiesSet();

        verify(scheduler, times(1))
                .scheduleTask(any(TaskConfiguration.class), any(Task.class));
    }

    @Test
    public void deprecatedQuartzJobGetsScheduled() throws TaskSchedulerException {
        TestQuartzJob job = new TestQuartzJob();
        when(applicationContext.getBeansWithAnnotation(ScheduledQuartzJob.class))
                .thenReturn(Collections.singletonMap("scheduled-job", job));

        ScheduledQuartzJob annotation = job.getClass().getAnnotation(ScheduledQuartzJob.class);
        assertNotNull("Job not annotated with @ScheduledQuartzJob", annotation);
        TaskScheduler scheduler = mock(TaskScheduler.class);

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