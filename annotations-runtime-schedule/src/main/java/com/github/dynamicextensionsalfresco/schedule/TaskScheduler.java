package com.github.dynamicextensionsalfresco.schedule;

public interface TaskScheduler {

    com.github.dynamicextensionsalfresco.schedule.TaskRegistration scheduleTask(
            com.github.dynamicextensionsalfresco.schedule.TaskConfiguration config, Object bean) throws com.github.dynamicextensionsalfresco.schedule.TaskSchedulerException;

}
