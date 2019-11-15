# Dynamic Extensions for Alfresco

[![Apache License 2](https://img.shields.io/badge/license-Apache%202-blue.svg)](LICENSE)
[![Travis Build Status](https://img.shields.io/travis/xenit-eu/dynamic-extensions-for-alfresco?label=Travis)](https://travis-ci.org/xenit-eu/dynamic-extensions-for-alfresco)
[![Jenkins Build Status](https://jenkins-2.xenit.eu/buildStatus/icon?job=Xenit+Github%2Fdynamic-extensions-for-alfresco%2Fmaster&subject=Jenkins)](https://jenkins-2.xenit.eu/job/Xenit%20Github/job/dynamic-extensions-for-alfresco/job/master/)
[![Maven Central](https://img.shields.io/maven-central/v/eu.xenit/alfresco-dynamic-extensions-repo-61.svg?maxAge=300)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22eu.xenit%22%20AND%20a%3A%22alfresco-dynamic-extensions-repo-*%22)

Add OSGi based hot-deploy functionality and Spring annotation based configuration to Alfresco.

## Introduction

Rapid development of Alfresco repository extensions in Java. Deploy your code in seconds, not minutes. Life is too short for endless server restarts.

Dynamic Extensions adds an OSGi container to the Alfresco repository, enabling live deployment of Java code, with no need to restart the server.
Alfresco itself is not "OSGi-fied" in any way; the OSGi container runs on top of the core Alfresco platform.

Standard Alfresco Platform Extensions use Spring XML based configuration. With Dynamic Extensions, developers
have the ability to create Alfresco Platform Extensions using Spring's annotations based configuration.

## Installing Dynamic Extensions in Alfresco

Dynamic Extensions (DE) is distributed as an 
[Alfresco Module Package (AMP)](https://docs.alfresco.com/5.2/concepts/dev-extensions-packaging-techniques-amps.html) extension that can be installed in Alfresco.

### Installing the Dynamic Extensions AMP

To support multiple Alfresco versions, different AMPs for each minor Alfresco version update are 
build and distributed.  
E.g. if you are working with Alfresco 6.0.7-ga, you should
use the `alfresco-dynamic-extensions-repo-60` artifact.

#### Maven Central Coordinates
All required artifacts, including the AMP to be installed in Alfresco, are available in Maven Central.

```xml
<dependency>
    <groupId>eu.xenit</groupId>
    <artifactId>alfresco-dynamic-extensions-repo-${alfresco-version}</artifactId>
    <version>${latest-dynamic-extensions-version}</version>
    <type>amp</type>
</dependency>
```

```groovy
alfrescoAmp "eu.xenit:alfresco-dynamic-extensions-repo-${alfrescoVersion}:${dynamicExtensionsVersion}@amp"

```

These artifacts can be used to automatically install Dynamic Extensions in Alfresco using e.g. the Alfresco Maven SDK or 
the [Alfresco Docker Gradle Plugins](https://github.com/xenit-eu/alfresco-docker-gradle-plugin)

#### Manual download and install

* Download the latest <a href="https://github.com/xenit-eu/dynamic-extensions-for-alfresco/releases">Dynamic Extensions AMP</a>.
* Use the <a href="https://docs.alfresco.com/6.1/concepts/dev-extensions-modules-management-tool.html">Module Management Tool</a> to install the AMP in the Alfresco repository of your choosing.
* After restarting Alfresco, open the Control Panel: <a href="http://localhost:8080/alfresco/service/dynamic-extensions/">http://localhost:8080/alfresco/service/dynamic-extensions/</a>.
* Accessing the Control Panel requires an admin account.

### Supported Alfresco versions

Dynamic Extensions is systematically integration-tested against:

* Alfresco Enterprise & Community 6.1 (Requires hotfix, see below)
* Alfresco Enterprise & Community 6.0
* Alfresco Enterprise & Community 5.2
* Alfresco Enterprise & Community 5.1
* Alfresco Enterprise & Community 5.0

> Integration tests are currently only executed on our private Jenkins build server. 

#### Known Alfresco issues that impact Dynamic Extensions
<details><summary>Alfresco 6.1 - wrong version of 'Commons annotations' used</summary>When using DE on Alfresco 6.1, it is possible that it fails to startup due to following error:

```
Caused by: java.lang.NoSuchMethodError: javax.annotation.Resource.lookup()Ljava/lang/String;
at org.springframework.context.annotation.CommonAnnotationBeanPostProcessor$ResourceElement.<init>(CommonAnnotationBeanPostProcessor.java:621)
at org.springframework.context.annotation.CommonAnnotationBeanPostProcessor.lambda$buildResourceMetadata$0(CommonAnnotationBeanPostProcessor.java:383)
at org.springframework.util.ReflectionUtils.doWithLocalFields(ReflectionUtils.java:719)
at org.springframework.context.annotation.CommonAnnotationBeanPostProcessor.buildResourceMetadata(CommonAnnotationBeanPostProcessor.java:365)
at org.springframework.context.annotation.CommonAnnotationBeanPostProcessor.findResourceMetadata(CommonAnnotationBeanPostProcessor.java:350)
at org.springframework.context.annotation.CommonAnnotationBeanPostProcessor.postProcessMergedBeanDefinition(CommonAnnotationBeanPostProcessor.java:298)
at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.applyMergedBeanDefinitionPostProcessors(AbstractAutowireCapableBeanFactory.java:1044)
at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:550)
```

The root cause is for this problem is that Alfresco has multiple implementations of the 
[JSR 250 specification](https://en.wikipedia.org/wiki/JSR_250), 'Common Annotations' in the `WEB-INF/lib/` folder:

1. javax.annotation:javax.annotation-api
2. javax.annotation:jsr250-api
3. org.apache.geronimo.specs:geronimo-annotation_1.0_spec

Only the first one is up to date and contains the correct implementation of the `Resource` class. The other two versions
contain an old implementation of the `Resource` class, causing the provided error to be thrown by Spring internally.

This is only an issue as of Java 11 (Alfresco 6.1) because earlier versions had an correct implementation 
of the `Resource` class embedded in the distribution, and the 
[`bootstrap` classloader has the highest priority](https://tomcat.apache.org/tomcat-9.0-doc/class-loader-howto.html).

This issue has been reported to Alfresco: [MNT-20557](https://issues.alfresco.com/jira/browse/MNT-20557). 
Waiting for Alfresco to fix the issue, following workarounds can be used to make DE work on Alfresco 6.1:

* Remove the `jsr250-api` and `geronimo-annotation_1.0_spec` jars from the `WEB-INF/lib` folder of the Alfresco webapp.
* Install [this hotfix AMP](https://github.com/xenit-eu/alfresco-hotfix-MNT-20557) in your Alfresco distribution, 
which will overwrite the `jsr250-api` and `geronimo-annotation_1.0_spec` jars with empty jars.
</details>

## Example Dynamic Extensions based Alfresco Platform extension

This example Web Script examines a node and passes information to a Freemarker template:
```java
@Component
@WebScript
public ExampleWebScript {

  @Autowired
  private NodeService nodeService;

  @Uri("/show-node")
  // Example: http://localhost/alfresco/service/show-node?nodeRef=workspace://SpacesStore/12345
  public Map<String, Object> displayNodeName(@RequestParam NodeRef nodeRef) {
    Map<String, Object> model = new HashMap<String, Object>();
    model.put("properties", nodeService.getProperties(nodeRef));    
    return model; // Model is passed to Freemarker template.
  }
}
```

Note that this is an _annotation Web Script_. These types of Web Script are configured through Java annotations instead of `*.desc.xml` descriptors. Annotation Web Scripts are similar to Spring MVC's annotation-based controllers.

Here's the accompanying Freemarker template fragment:

```html
<table>
  <#list properties?keys as name>    
    <tr>
      <th>${name}</th>
      <td>${properties[name]!''}</td>
    </tr>
  </#list>
</table>
```

This is all the code that is required; there's no need for Spring XML config or Web Script XML descriptors. Hot-reloading and reducing configuration overhead are not particularly novel concepts in the Java development world at large. Essentially, Dynamic Extensions modernizes the development of Alfresco repository extensions.

The example above may be trivial, but the point is that, behind the scenes, services are still wired together through Spring and handled by the Web Script framework. Conceptually there is no real difference between a Dynamic Extension and a regular Alfresco extension. There's just less overhead and more convenience.

## Documentation
Please checkout the [documentation](documentation/README.md) for further instructions.

## License 
This project is licensed under the Apache V2 License - see the [LICENSE](LICENSE) file for details.

## Useful links
* [Youtube - One hour presentation on using and exploring Dynamic Extensions for Alfresco](https://www.youtube.com/watch?v=Pc62PM7U3Ns)
* [Dynamic Extensions custom Bundle example project](https://github.com/xenit-eu/example-dynamic-extension)
