# Scheduled Jobs

## Using Dynamic Extensions Annotations to Schedule Jobs

To schedule the execution of some logic, annotate the class implementing the logic with `@ScheduledTask` 
and implement the `com.github.dynamicextensionsalfresco.schedule.Task` interface. 

Dynamic Extension supports cron expressions of the Quartz format. The `cron` parameter should therefore be a valid
Quartz cron expression. 

```java
import com.github.dynamicextensionsalfresco.schedule.ScheduledTask;
import com.github.dynamicextensionsalfresco.schedule.Task;

@Component
@ScheduledTask(name = "example", cron = "0/15 * * * * ?")
public class ScheduledExampleTask implements Task {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledExampleTask.class);

    @Autowired
    private NodeService nodeService;

    @Override
    public void execute() {
        logger.info("Executing '{}'", ScheduledExampleTask.class.getCanonicalName());
    }
}
```

Alternatively, the cron expression can be externalized to the `alfresco-global.properties`. Use the `cronProp` property 
of the `ScheduledTask` annotation to indicate which property should be used as cron expression. 


```java
...
@Component
@ScheduledTask(name = "example", cronProp = "eu.xenit.example.cron", cron = "0/15 * * * * ?")
public class ScheduledExampleTask implements Task {
...
```

If the property `eu.xenit.example.cron` is available it's value is used as cron expression. Otherwise DE will 
fallback to the value of the `cron` parameter as a default. 
 
> The `@ScheduledQuartzJob` annotation has been deprecated since Dynamic Extensions 2.0 and replace by the 
> vendor neutral `@ScheduledTask` annotation. It still works on 2.0, but is scheduled for removing in later versions.

## Implementation Notes

Dynamic Extensions bundles should delegate the scheduling work to the DE Platform, rather than creating new - or 
accessing existing Quartz schedulers directly.

Should one need, for some reason, to access the scheduler directly, a simple `@Autowired` will not be sufficient, 
due to the fact that there might be multiple beans of the same type.
To get access to the Quartz Scheduler bean, please use `@Autowire` in combination with the `@Qualifier` notation:
```java
@Autowired
@Qualifier("schedulerFactory")
private Scheduler scheduler;
``` 