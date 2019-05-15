# Extension Dependencies

## Extension classpath

Dynamic extensions can use any library present in Alfresco's webapp classpath. (not tomcat shared)

When you first install dynamic extensions, it scans the webapp classpath for libraries and stores the results in the repository. (for performance)

Whenever the `WEB-INF/lib` changes, the list of libraries is refreshed upon restart. This means you can simply restart Alfresco after adding/removing a jar to `WEB-INF/lib` folder.

You can consult the complete list at <http://localhost:8080/alfresco/service/dynamic-extensions/container/system-packages>.

A more modular and dynamic way of providing dependencies is by deploying the dependency as an OSGi bundle.
However not any jar file can be deployed as a bundle. It needs to have an OSGi manifest.
Many of the common Java libraries come with a compliant manifest, but not all.

Since version 1.0 of Dynamic Extensions, the control panel will attempt to auto-transform any regular jar file your upload into a valid OSGi bundle.
(BND is used behind the scenes)

The filename is used as a Symbolic-Name. (required for OSGi)

Should you want to perform this manually, (to get more control), you can use the BND tools manually.

Let's take JUnit as an example. Upload the junit jar using <http://localhost:8080/alfresco/service/dynamic-extensions/bundles>.
The operation will fail with:
```
Error installing Bundle: Could not generate Bundle filename. Make sure the content is an OSGi bundle.
```

To transform the jar into a bundle, we'll use the **bnd** tool. First, download <https://bndtools.ci.cloudbees.com/job/bnd.master/lastSuccessfulBuild/artifact/dist/bundles/biz/aQute/bnd/biz.aQute.bnd/>.

To have a look at the existing manifest:
```
java -jar bnd.jar print junit.jar

[MANIFEST junit-4.11]
Ant-Version                              Apache Ant 1.8.2                        
Created-By                               1.7.0_04-b20 (Oracle Corporation)       
Manifest-Version                         1.0                                     

[IMPEXP]
```

We can see no Import/Export statements, so there is no way for the OSGi container to resolve dependencies.
Using **bnd**'s wrap command, we can generate the required manifest and create a compliant jar file:
```
java -jar bnd.jar wrap -b JUnit -o junit-osgi.jar junit.jar

java -jar bnd.jar print junit-osgi.jar

[MANIFEST junit-osgi]
Ant-Version                              Apache Ant 1.8.2                        
Bnd-LastModified                         1384167913446                           
Bundle-ManifestVersion                   2                                       
Bundle-Name                              JUnit                                   
Bundle-SymbolicName                      JUnit                                   
Bundle-Version                           0                                       
Created-By                               1.7.0_45 (Oracle Corporation)           
Export-Package                           LICENSE.txt,junit.extensions;uses:="junit.framework",junit.framework;uses:="org.junit.runner,org.junit.runner.manipulation,org.junit.runner.notificat
...
```

We can now finally deploy junit-osgi.jar and extensions using the junit classes. This approach requires no restarts and also allows for deploying/using multiple versions of a library.

A final benefit is the isolation towards Alfresco: none of the deployed OSGi libraries will be visible to Alfresco and thus we can safely deploy newer versions of eg. Guava without conflicting with Alfresco code dependencies.

You can avoid the use of **bnd** by first checking the Spring bundle repository: <http://ebr.springsource.com/repository/app/>. They host OSGi versions of many popular Java libraries.

## ClassNotFoundException

When using libraries such as Hibernate or Spring's @Configuration support, you may encounter `ClassNotFoundException`. (ie. references to cglib)

The reason for this is dynamic loading of classes that the bnd tool can not detect from your compiled .class files.

The Gradle plugin sets the `instruction 'DynamicImport-Package', '*'` by default since DE 1.3.

This means that, if you did not override the `Import-Package` instruction, the classes need to be added to the system classpath: `WEB-INF/lib`.