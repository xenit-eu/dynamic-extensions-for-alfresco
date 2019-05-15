# Alfresco version specific beans

Dynamic Extensions provides the possibility to initialize some beans only on 
specific Alfresco versions. The `@AlfrescoPlatform` annotation indicates 
for which Alfresco version range a specific bean (annotated with e.g. `@Component`)
should be initialized.

```java
@Component
@AlfrescoPlatform(minVersion = "6.0")
public class Alfresco6SpecificBean {

}
```