# Service proxies

## Use case
Services defined within a Dynamic Extension (Bundle), run within their own private Spring ApplicationContext.

This means they are not visible from the global Alfresco Spring context. (much like Alfresco subsystems)

In some cases you want your service to be visible to other beans running in the global Alfresco context:
* you want to override an existing bean
* you want to integrate with a non-dynamic extension

## Proxy to the rescue
Alfresco subsystems like the Solr context, are wired into the global context via a Proxy: a proxy bean is defined in the global context and configured with the target bean name and target subcontext. (search -> solr/search)

This approach can almost be reused for Dynamic Extensions. We configure a proxy with a service filter (ie. all beans implementing com.IReplicator) and all calls to that proxy are forwarded to the embedded service.

Note: some exposure to OSGi terminology lies ahead

## Caveats
Unlike Alfresco subsystems, Dynamic Extensions are started asynchronously + they come and go as they please: you can uninstall, update or install alternative implementations of a particular service. So we cannot guarantee that a service will be available when the proxy is invoked.

Dependencies can be expressed between extensions, but not between the 2 worlds. (Alfresco global context & DE) 

This is a limitation we have to accept. Any calls to the proxy, when the service is not available, will fail with an `IllegalStateException`

## Registering a proxy
### Publication
By default, any bean tagged with `@Component` is registered within the local Spring context, but remains private to the extension.

An additional step is required to export it: `@OsgiService`
```java
@Component
@OsgiService
public class DefaultReplicator implements IReplicator {
   ...
}
```

### Consumption
In an Alfresco x-context.xml file, register a proxy:
```xml
<bean id="replicator" parent="abstractBundleServiceProxy">
  <property name="targetInterfaces">
    <list>
      <value>com.IReplicator</value>
    </list>
  </property>
</bean>
```

## Matching
If exactly 1 `IReplicator` implementation is published using the `@OsgiService` annotation, we are done.

In some scenarios, you can have multiple implementations for a service running at the same time. (ie. lucene & solr)

We now need a more specific filter, to link both components.

### Publication
Add extra details to the service publication:
```java
@OsgiService(headers = @OsgiService.ExportHeader(key = "transactional", value = "true"))
public class LuceneSearch implements SearchService {}
```

### Consumption
```xml
<bean id="search" parent="abstractBundleServiceProxy">
  <property name="targetInterfaces">
    <list>
      <value>SearchService</value>
    </list>
  </property>
  <property name="filterString" value="(&amp;(objectClass=SearchService)(transactional=true))"/>
</bean>
```
**objectClass** selects by implemented interface

## Update filter at runtime
You can update the configured filter at runtime, by casting the proxy to a `FilterModel`.

## Classloader constraints
As Alfresco's classloader is the parent of all extension classloaders, classes defined in the web application classpath (WEB-INF/lib or WEB-INF/classes) are visible to both. Classes implemented within an extension, are only visible to that extension (or other extensions), not to Alfresco.

For this reason, you should provide your proxy interfaces (IReplicator) to the webapp classpath. (deployed via regular AMP file)
Of course, if you're using interfaces defined by Alfresco, the JDK or WEB-INF/lib libraries, this is already the case.

The implementation classes can remain inside your extension.