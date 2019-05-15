# Reusing Spring services or components

For simple use cases, annotating a service with `@Component` or `@Service` is all we need to do to inject them into other components using `@Autowired`. But how can we use services from other extensions ?

## Injecting OSGi services

If you want to move common services to a separate extension (= OSGi Bundle), you can mark public services using the `@OsgiService` annotation.

By default all Spring beans are only visible to other beans in the same extension Spring context, but this annotation will publish your service as an OSGi services, meaning it can be accessed from any other extension.

Note that this makes the importing extension dependent on the life cycle of the exporting extension: if the Spring context for the exported has not started, no services will be available to the importing extension.

Note that it is best practice to define an interface for your service and autowire using that interface.
While not a strict requirement at the moment, it could be in the future.

## Reusing services as libraries

An alternative strategy is to redefine a class as a new Spring bean within the importing extension.

To do so, you can:
* override the class in your extension and annotate as a component
* define a Spring bean in xml
* use `@Configuration` aka Spring Java configuration

Let's look at the last option:
```java
@Configuration
public class SpringConfiguration {
    @Autowired
    NodeService nodeService;

    /**
     * Reuse commons.NodeLocator
     */
    @Bean
    public NodeLocator nodeLocator() {
        // class implementation is defined in another extension
        return new NodeLocator(nodeService);
    }
}
```