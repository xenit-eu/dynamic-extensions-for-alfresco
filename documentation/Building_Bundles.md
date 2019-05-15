# Building Dynamic Extensions compatible Bundles

## Building DE OSGi Bundles

Dynamic Extensions uses the OSGi specification to enable hot reloading of Java 
code. Therefore, [OSGi bundles](https://en.wikipedia.org/wiki/OSGi#Bundles) 
are the unit of deployment for Dynamic Extensions Alfresco extension points.

Dynamic Extensions Bundles are build by just adding some custom `META-INF/MANIFEST.MF` headers
to the standard OSGi Bundle headers.

* `Alfresco-Dynamic-Extension: true`  
    Indicates that an OSGi Bundle is a 'Dynamic Alfresco Extension' and enables the 
    Alfresco integration.
    
* `Alfresco-Spring-Configuration: eu.xenit.de.example`  
    This header can be used to indicate which packages of your Bundle need to be scanned for Spring beans.  
    If this header is not present, Dynamic Extensions will scan for Spring XML configuration files in the 
    `/META-INF/spring` directory of the Bundle.  
    If the header is not provided and there is not Spring XML configuration present in the bundle, all the packages
    inside the Bundle will be scanned for Spring beans.
    

### Building DE OSGi Bundles using Gradle

// TODO

### Building DE OSGi Bundles using Maven

Dynamic Extensions does not provide a maintained Maven equivalent for the Gradle plugin. 
Nevertheless, since DE Bundles are basically just OSGi bundles, the 
`org.apache.felix:maven-bundle-plugin` plugin can be used to build Dynamic Extensions compatible 
Bundles.

```xml
    <plugin>
        <groupId>org.apache.felix</groupId>
        <artifactId>maven-bundle-plugin</artifactId>
        <version>${version}</version>
        <!-- This option enables custom bundle headers in the <instructions> -->
        <extensions>true</extensions>
        <configuration>
            <instructions>
                <Alfresco-Dynamic-Extension>true</Alfresco-Dynamic-Extension>
                <Alfresco-Spring-Configuration>eu.xenit.de.example</Alfresco-Spring-Configuration>
                
                <!-- Optionally, the Package headers can be customized
                <Import-Package></Import-Package>
                <Export-Package></Export-Package>                
                 -->
            </instructions>
        </configuration>
    </plugin>
```

Have a look at the plugin's [detailed documentation](https://felix.apache.org/documentation/subprojects/apache-felix-maven-bundle-plugin-bnd.html). 
for more information.

## Assembling DE Bundles into an AMP

To package DE Bundles into an AMP, all that need to happen is to put the DE Bundle inside the 
`config/dynamic-extensions/bundles` directory inside the AMP.

### Assembling Bundles into an AMP using Gradle

The Alfresco Gradle SDK has support for building AMP's with DE Bundles. Please have a look at the 
[corresponding documentation](https://github.com/xenit-eu/alfresco-gradle-sdk#dynamicextension) in that project.

### Assembling Bundles into an AMP using Maven

The Alfresco Maven SDK uses the Maven `org.apache.maven.plugins:maven-assembly-plugin` plugin to build custom 
AMP's. DE Bundles that need to be packaged into an AMP, can easily be build by extending the `amp.xml` 
descriptor file with a `dependencySet` that has the correct `outputDirectory`

```xml
    <dependencySets>
        <dependencySet>
            <outputDirectory>config/dynamic-extensions/bundles</outputDirectory>
            <includes>
                <include>${project.groupId}:${project.artifactId}</include>
            </includes>
        </dependencySet>
    </dependencySets>
```
