# Scheduled Jobs

To schedule the execution of some logic, annotate the class implementing the logic with `@ScheduledTask` 
and implement the `com.github.dynamicextensionsalfresco.schedule.Task` interface. 

Dynamic Extension supports cron expressions of the Quart format. The `cron` parameter should therefore be a valid
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
 
> The `@ScheduledQuartzJob` annotation has been deprecated since Dynamic Extensions 2.0 and replace by the 
> vendor neutral `@ScheduledTask` annotation. It still works on 2.0, but is scheduled for removing in later versions.