package com.github.dynamicextensionsalfresco.schedule;

import com.github.dynamicextensionsalfresco.jobs.ScheduledQuartzJob;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


/**
 * Registrar that takes care of {@link ScheduledTask} annotated beans that implement {@link Task} in DE-bundles.
 *
 * For backwards compatability with the DE 1.x API, support for {@link ScheduledQuartzJob} annotated beans that
 * implement org.schedule.Job is retained, but is deprecated. If your DE-bundle targets both Alfresco 5.x and 6.x
 * it's recommended to use {@link ScheduledTask} instead.
 */
public class ScheduledTaskRegistrar implements ApplicationContextAware, InitializingBean, DisposableBean {

    private Logger logger = LoggerFactory.getLogger(ScheduledTaskRegistrar.class);

    @Autowired
    protected TaskScheduler scheduler;

    @Autowired
    @Qualifier("global-properties")
    protected Properties globalProperties = new Properties();

    private ArrayList<TaskRegistration> registeredTasks = new ArrayList<>();

    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() {
        this.registerScheduledTaskAnnotatedBeans();
    }

    public void registerScheduledTaskAnnotatedBeans() {
        Map<String, Object> scheduledBeans = applicationContext.getBeansWithAnnotation(ScheduledQuartzJob.class);
        for (Map.Entry entry : scheduledBeans.entrySet()) {
            Object bean = entry.getValue();
            try {
                this.registerTask(bean);
            } catch (Exception e) {
                logger.error("Failed to register job: ", e);
            }
        }
    }

    TaskRegistration registerTask(Object bean) throws TaskSchedulerException {
        if (bean == null) throw new IllegalArgumentException("argument 'bean' is null");

        TaskConfiguration config = getTaskConfiguration(bean);

        TaskRegistration registration = scheduler.scheduleTask(config, bean);
        registeredTasks.add(registration);

        logger.debug("Scheduled task {} from group {} with cron {}",
                config.getName(), config.getGroup(), config.getCron());

        return registration;
    }

    TaskConfiguration getTaskConfiguration(Object bean) {
        TaskConfiguration.Builder builder = TaskConfiguration.builder();

        ScheduledTask annotation = bean.getClass().getAnnotation(ScheduledTask.class);
        if (annotation != null) {
            builder.withAnnotation(annotation, globalProperties);
        } else {
            // fallback to deprecated @ScheduledQuartzJob
            ScheduledQuartzJob quartzJobAnnotation = bean.getClass().getAnnotation(ScheduledQuartzJob.class);
            if (quartzJobAnnotation == null) {
                throw new IllegalArgumentException("Annotation @ScheduledTask missing on bean %s");
            }
            builder.withAnnotation(quartzJobAnnotation, globalProperties);
        }

        return builder.build();
    }

    @Override
    public void destroy() {

        logger.info("Unscheduling {} tasks", registeredTasks.size());

        for (TaskRegistration task : registeredTasks) {
            try {
                task.unregister();
            } catch (TaskSchedulerException e) {
                logger.warn("Failed to unschedule task: {}", e);
            }
        }
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void setScheduler(TaskScheduler scheduler) {
        this.scheduler = scheduler;
    }
}