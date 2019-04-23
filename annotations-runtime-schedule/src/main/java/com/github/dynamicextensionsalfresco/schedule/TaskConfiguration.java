package com.github.dynamicextensionsalfresco.schedule;

import com.github.dynamicextensionsalfresco.jobs.ScheduledQuartzJob;
import java.util.Properties;

public class TaskConfiguration {

    /**
     * A cron-like expression, extending the usual UN*X definition to include triggers on the second as well as minute,
     * hour, day of month, month and day of week.  e.g. <code>"0 * * * * MON-FRI"</code> means once per minute on
     * weekdays (at the top of the minute - the 0th second).
     */
    private final String cron;

    /**
     * the unique name of the Quartz job shown in JMX/admin page
     */
    private final String name;

    /**
     * the job group name, defaults to "DEFAULT"
     */
    private final String group;

    /**
     * If true the job will only run once in the Alfresco cluster by using the JobLockService
     */
    private final boolean cluster;

    public TaskConfiguration(String cron, String name, String group, boolean cluster) {
        this.cron = cron;
        this.name = name;
        this.group = group;
        this.cluster = cluster;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getCron() {
        return cron;
    }

    public String getName() {
        return name;
    }

    public String getGroup() {
        return group;
    }

    public boolean isCluster() {
        return cluster;
    }

    public static class Builder {
        private String cron = "";
        private String name = "";
        private String group = "DEFAULT";
        private boolean cluster = false;

        private Builder() {

        }

        public TaskConfiguration build() {
            return new TaskConfiguration(cron, name, group, cluster);
        }

        public Builder withCron(String cron) {
            this.cron = cron;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withGroup(String group) {
            this.group = group;
            return this;
        }

        public Builder withCluster(boolean cluster) {
            this.cluster = cluster;
            return this;
        }

        public Builder withAnnotation(ScheduledQuartzJob annotation, Properties properties) {
            return this
                    .withName(annotation.name())
                    .withGroup(annotation.group())
                    .withCron(properties.getProperty(annotation.cronProp(), annotation.cron()))
                    .withCluster(annotation.cluster());
        }

        public Builder withAnnotation(ScheduledTask annotation, Properties properties) {
            return this
                    .withName(annotation.name())
                    .withGroup(annotation.group())
                    .withCron(properties.getProperty(annotation.cronProp(), annotation.cron()))
                    .withCluster(annotation.cluster());
        }
    }
}
