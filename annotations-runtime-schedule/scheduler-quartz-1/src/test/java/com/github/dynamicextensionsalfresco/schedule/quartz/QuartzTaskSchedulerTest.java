package com.github.dynamicextensionsalfresco.schedule.quartz;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.github.dynamicextensionsalfresco.schedule.Task;
import com.github.dynamicextensionsalfresco.schedule.TaskConfiguration;
import com.github.dynamicextensionsalfresco.schedule.TaskRegistration;
import com.github.dynamicextensionsalfresco.schedule.TaskSchedulerException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.junit.Test;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

public class QuartzTaskSchedulerTest {

    @Test
    public void testQuartzIntegration() throws TaskSchedulerException, SchedulerException, InterruptedException {
        Scheduler scheduler = spy(StdSchedulerFactory.getDefaultScheduler());
        scheduler.start();

        // synchronization primitives: quartz executes jobs is a background thread
        Lock lock = new ReentrantLock();
        Condition monitor = lock.newCondition();

        QuartzTaskScheduler quartzTaskScheduler = new QuartzTaskScheduler();
        quartzTaskScheduler.setScheduler(scheduler);

        TaskConfiguration config = new TaskConfiguration("*/15 * * ? * *", "name", "group", false);

        // Schedule the job
        AtomicBoolean executed = new AtomicBoolean(false);
        TaskRegistration task = quartzTaskScheduler.scheduleTask(config, (Job) jobExecutionContext -> {
            lock.lock();
            try {
                executed.set(true);
                monitor.signal();
            } finally {
                lock.unlock();
            }
        });

        // Verify the job got scheduled
        verify(scheduler, times(1)).scheduleJob(any(JobDetail.class), any(Trigger.class));

        // Manually trigger the job
        // but take the lock first, so we can easily wait for the result
        lock.lock();
        task.trigger();

        try {
            boolean beforeTimeout = monitor.await(2, TimeUnit.SECONDS);
            if (!beforeTimeout) {
                fail("Timeout waiting for job to complete");
            }
        } finally {
            lock.unlock();
        }

        assertTrue("Job was not executed", executed.get());
        scheduler.shutdown();
    }

    @Test
    public void testScheduleTask() throws TaskSchedulerException {
        Scheduler scheduler = new FakeQuartzScheduler();

        QuartzTaskScheduler quartzTaskScheduler = new QuartzTaskScheduler();
        quartzTaskScheduler.setScheduler(scheduler);

        TestTask task = new TestTask();
        TaskRegistration registration = quartzTaskScheduler.scheduleTask(
                new TaskConfiguration("*/15 * * ? * *", "name", "group", false),
                task);

        registration.trigger();
        assertTrue("Job was not executed", task.isExecuted());
    }

    @Test
    public void testScheduleJob() throws TaskSchedulerException {
        Scheduler scheduler = new FakeQuartzScheduler();

        QuartzTaskScheduler quartzTaskScheduler = new QuartzTaskScheduler();
        quartzTaskScheduler.setScheduler(scheduler);

        // Schedule the job
        TestJob job = new TestJob();
        TaskRegistration registration = quartzTaskScheduler.scheduleTask(
                new TaskConfiguration("*/15 * * ? * *", "name", "group", false),
                job);

        registration.trigger();
        assertTrue("Task was not executed", job.isExecuted());
    }


    class TestTask implements Task {

        private boolean executed = false;

        @Override
        public void execute() {
            this.executed = true;
        }

        boolean isExecuted() {
            return executed;
        }
    }

    class TestJob implements Job {

        private boolean executed = false;

        @Override
        public void execute(JobExecutionContext jobExecutionContext) {
            this.executed = true;
        }

        boolean isExecuted() {
            return executed;
        }
    }
}