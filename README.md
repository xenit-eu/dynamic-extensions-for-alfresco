Maven repository for Dynamic Extensions for Alfresco
===================================================

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
  <version>1.0.0.M3</version>
  <scope>provided</scope>
</dependency>
```

For more details see the <a href="https://github.com/lfridael/example-dynamic-extension/blob/master/pom.xml">pom.xml</a> for the <a href="https://github.com/lfridael/dynamic-extensions-for-alfresco">example Dynamic Extension</a>.
