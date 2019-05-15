# Content Model

Dynamic Extensions provides a convenient way of registering custom content models with the Alfresco Data Dictionary. (See the <a href="https://docs.alfresco.com/5.0/concepts/content-modeling-about.html?m=2">Content Modeling</a> for more info on content models.)

To have XML content models registered automatically, place them in this location in your extension:

<code>/META-INF/alfresco/models</code> <a href="https://github.com/xenit-eu/example-dynamic-extension/tree/master/src/main/resources/META-INF/alfresco/models">See example project</a>

When Dynamic Extensions installs an extension it scans for the presence of XML files in this folder and registers/updates the content models with the Data Dictionary. Uninstalling an extension does not remove the model as this would cause the model to disappear during restarts.

Dynamic Extensions assumes the XML files in this folder contain content models, any other XML content will yield an error. It ignores everything in this folder that is not an XML file.

## Reference

* [Content modeling](https://docs.alfresco.com/5.0/concepts/content-modeling-about.html?m=2) (Alfresco Documentation)

Note that you cannot make breaking changes to your model. Alfresco will refuse to update the model in this case.
(same behaviour as manual upload to Data Dictionary / Models)

## Direct Model DAO access

During development, it can be usefull to bypass the registration of models via the repository when doing heavy refactoring of a model. As Alfresco does not allow breaking changes (such as adding a required aspect or removing types that are in use), you can opt for a bypass of this validation during __development__.

To do so, override the default RepositoryModelRegistrar by adding this component definition to your project.

This will cause models to be unregistered whenever your extension is stopped (or updated).

```java
@Component("modelRegistrar")
public class ModelRegistrar extends DAOModelRegistrar implements InitializingBean {
    @Autowired
    public ModelRegistrar(DictionaryDAO dictionaryDAO, M2ModelListProvider m2ModelListProvider) {
        super(dictionaryDAO, m2ModelListProvider);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        registerModels();
    }
}
```

**Note that this can break you data consistency and thus should only be used on a local development Alfresco !**

# Translations

As of DE 1.3, you can provision your model translation files (.properties) in the `/META-INF/alfresco/messages` path of your extension jar.

Any property files in this path will be stored in the `Data Dictionary / Messages` repository folder and registered with the Alfresco `MessageService`.

_this feature remains unpredictable due to the indexing latency of Alfresco: https://github.com/laurentvdl/dynamic-extensions-for-alfresco/issues/116_
