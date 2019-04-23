package com.github.dynamicextensionsalfresco.schedule;

public interface TaskRegistration {

    /**
     * Unschedule a previously scheduled task
     */
    void unregister() throws TaskSchedulerException;

    void trigger() throws TaskSchedulerException;
}
