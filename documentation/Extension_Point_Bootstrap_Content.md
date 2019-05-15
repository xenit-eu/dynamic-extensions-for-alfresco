# Bootstrap Content

Often you want some default nodes to be created (or updated) when your extension is started.

Both the dynamic module registry as the workflow registry deploy resources from your extension to the repository upon startup.

They use the `BootstrapService` for this. This service takes a strategy for determining if a resource should be updated when it already exists as a node.

Here is example of deploying some xml files:

```java
@Component
public class MyBootstrap implements InitializingBean {
  @Autowired
  protected ResourceHelper resourceHelper;

  @Autowired
  protected BootstrapService bootstrapService;

  public void afterPropertiesSet() {
    bootstrapService.deployResources(
          "osgibundle:/META-INF/alfresco/bootstrap/*.xml",
          new RepositoryLocation(
              StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, 
              "/app:company_home/app:dictionary", 
              SearchService.LANGUAGE_XPATH
          ),
          new ContentCompareStrategy(resourceHelper),
          null, null, ContentModel.TYPE_CONTENT
    );
  }
}
```

First, we specify the source: you can use a Spring resource pattern here: osgibundle is used to reference the extension scope. In this case the jar of the extension.
All xml files in `/META-INF/alfresco/bootstrap` will be copied to the repository.

Next, specify a target location: XPATH makes a good candidate here.

Now decide on a strategy. To update nodes when the content has changed, use `ContentCompareStrategy`. To always update, use `StaticUpdateStrategy`.

The encoding and mimetype will be autodetected if left blank.

And finally you need to specify the type for any newly created nodes.