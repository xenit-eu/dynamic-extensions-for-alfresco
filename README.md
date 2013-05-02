Dynamic Extensions for Alfresco - Maven repository
==================================================

# Gradle-based projects

Use the Alfreco Dynamic Extensions Gradle plugin to build Alfresco repository extensions.

More information: <a href="https://github.com/lfridael/dynamic-extensions-for-alfresco/wiki/Building-Alfresco-repository-extensions-for-Dynamic-Extensions-using-Gradle">Building Alfresco repository extensions for Dynamic-Extensions using Gradle</a>

# Maven-based projects

Add this entry to your `<repositories>` section in your Maven `pom.xml`: 

```xml
<repository>
  <id>dynamic-extensions-for-alfresco-mvn-repo</id>
  <url>https://raw.github.com/lfridael/dynamic-extensions-for-alfresco/mvn-repo/</url>
</repository>
```

Then add entries to your `<dependencies>` as follows:

```xml
<dependency>
  <groupId>nl.runnable.alfresco.dynamicextensions</groupId>
  <artifactId>annotations</artifactId>
  <version>1.0.0.M4</version>
  <scope>provided</scope>
</dependency>
```
