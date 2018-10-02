package com.github.dynamicextensionsalfresco.jobs;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
     * A cron-like expression, extending the usual UN*X definition to include triggers on the second as well as minute,
     * hour, day of month, month and day of week.  e.g. <code>"0 * * * * MON-FRI"</code> means once per minute on
     * weekdays (at the top of the minute - the 0th second).
     *
     * @return an expression that can be parsed to a cron schedule
     */
    String cron();

    /**
     * @return the unique name of the Quartz job shown in JMX/admin page
     */
    String name();

    /**
     * @return the job group name, defaults to "DEFAULT"
     */
    String group() default "DEFAULT";

    /**
     * A key to a property from alfresco-global.properties specifying a cron-like expression, extending the usual UN*X
     * definition to include triggers on the second as well as minute, hour, day of month, month and day of week.  e.g.
     * <code>"0 * * * * MON-FRI"</code> means once per minute on weekdays (at the top of the minute - the 0th second).
     *
     * @return an expression that can be parsed to a cron schedule. When not provided / not resolved, fallback to
     * default cron expression.
     */
    String cronProp() default "";

    /**
     * If true the job will only run once in the Alfresco cluster by using the JobLockService
     */
    boolean cluster() default false;
}
