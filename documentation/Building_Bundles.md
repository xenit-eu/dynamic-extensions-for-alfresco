# Building Dynamic Extensions compatible Bundles

## Required Java dependencies

To be able to use the Dynamic Extensions annotations and webscripts, following two dependencies are required
to compile a Dynamic Extensions Java Bundle:

```xml
        <dependency>
            <groupId>eu.xenit.de</groupId>
            <artifactId>annotations</artifactId>
            <version>${dynamic.extensions.version}</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>eu.xenit.de</groupId>
            <artifactId>webscripts</artifactId>
            <version>${dynamic.extensions.version}</version>
            <scope>provided</scope>
        </dependency>
```

When using the [Dynamic Extensions Gradle plugin](#Building DE OSGi Bundles using Gradle), 
these dependencies will automatically be included to your project.

## Building DE OSGi Bundles

Dynamic Extensions uses the OSGi specification to enable hot reloading of Java 
code. Therefore, [OSGi bundles](https://en.wikipedia.org/wiki/OSGi#Bundles) 
are the unit of deployment for Dynamic Extensions Alfresco extension points.

Dynamic Extensions Bundles are built by adding custom `META-INF/MANIFEST.MF` headers
to the standard OSGi Bundle headers.

* `Alfresco-Dynamic-Extension: true`  
    Indicates that an OSGi Bundle is a 'Dynamic Alfresco Extension' and enables the 
    Alfresco integration.
    
* `Alfresco-Spring-Configuration: eu.xenit.de.example`  
    This optional header can be used to indicate which packages of your Bundle need to be scanned for Spring beans.  
    If this header is not present, Dynamic Extensions will scan for Spring XML configuration files in the 
    `/META-INF/spring` directory of the Bundle.  
    If there also is no Spring XML configuration present in the bundle, all the packages
    inside the Bundle will be scanned for Spring beans.
    

### Building DE OSGi Bundles using Gradle

You can build Dynamic Extensions by applying the `eu.xenit.de` Gradle plugin.

The plugin will automatically add dependencies on the Dynamic Extensions packages and extend the `jar` task to enable OSGi bundling.

You will have to configure either `Alfresco-Spring-Configuration` (recommended)
or add the packages containing your Spring components to `Export-Package`.

```groovy
plugins {
    id "eu.xenit.de" version "2.0.0" // For the latest version, see: https://plugins.gradle.org/plugin/eu.xenit.de
}

jar {
    bnd(
        'Alfresco-Spring-Configuration': 'eu.xenit.de.example'
    )
}
```

Detailed documentation can be found in the [Gradle Plugin documentation](./Gradle_Plugin.md)

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

Have a look at the [detailed documentation of the bundle plugin](https://felix.apache.org/documentation/subprojects/apache-felix-maven-bundle-plugin-bnd.html)
for more information.

## Assembling DE Bundles into an AMP

To package DE Bundles into an AMP, all that need to happen is to put the DE Bundle inside the 
`config/dynamic-extensions/bundles` directory inside the AMP.

### Assembling Bundles into an AMP using Gradle

The Alfresco Gradle SDK has support for building AMPs with DE Bundles. Please have a look at the 
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
