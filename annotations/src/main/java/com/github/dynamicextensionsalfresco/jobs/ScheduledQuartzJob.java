package com.github.dynamicextensionsalfresco.jobs;

import java.lang.annotation.*;

/**
 * Register any class implementing {@link org.quartz.Job} scheduled Quartz jobs.
 *
 * @author Laurent Van der Linden
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ScheduledQuartzJob {

	/**
	 * A cron-like expression, extending the usual UN*X definition to include
	 * triggers on the second as well as minute, hour, day of month, month
	 * and day of week.  e.g. <code>"0 * * * * MON-FRI"</code> means once
	 * per minute on weekdays (at the top of the minute - the 0th second).
	 * @return an expression that can be parsed to a cron schedule
	 */
	String cron();

	/**
	 * @return the unique name of the Quartz job shown in JMX/admin page
	 */
	String name();

    /**
     * @return the job group name, defaults to {@link org.quartz.Scheduler#DEFAULT_GROUP}
     */
    String group() default org.quartz.Scheduler.DEFAULT_GROUP;
}
