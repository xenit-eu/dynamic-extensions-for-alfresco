# Static web resources

## Including resources such as css & js

Before DE 1.3, you could serve static files from your extension by extending the `AbstractBundleResourceHandler` class and mapping the internal jar path to a url.

From 1.3, any resources stored at `META-INF/alfresco/web` are made available at a default url:
`/alfresco/service/<symbolic-bundle-name>/web/`.

You can find this url on the webscripts (/alfresco/service/dynamic-extensions/webscripts) index under the `static-web` family.
Basic directory browsing is supported.

An alternative `/web-cached/{version}/` url is also available to enable versioning of resources and thus infinite caching: any resources accessed via the `web-cached` url has an expiration time set to now +1 year. By using ie. the bundle `Last-Modified` as a version segment, you can ensure users always have uptodate resources and they won't download the same version of a file twice.

For example:
`http://localhost:8080/alfresco/service/my-extension/web-cached/1434466040000/scripts/moment/moment.min.js`

Retrieve the bundle timestamp using `bundleContext.getBundle().getHeaders().get("Bnd-LastModified")`.
(bundleContext can be @Autowired)

Note that the webscript responsible for serving those files requires no authentication. So if you want to protect static files, store them at a different path and extend the `AbstractBundleResourceHandler` class.


## Serving static resources explicitly

You can package your resources in your extension jar file. 
To make these resources available to browsers, we can override the template resource webscript.

Two methods must be implemented:
* handleResources: the actual webscript url hook, the uri will determine the base url for all your resources
* getBundleEntryPath: translate resource uris to the path inside your jar

```java
@Component
@WebScript
@Authentication(AuthenticationType.NONE)
@Transaction(TransactionType.NONE)
public class Resources extends AbstractBundleResourceHandler {
    private final String packagePath = this.getClass().getPackage().getName().replace('.', '/');

    @Uri(value = "/my-extension/resources/{path}", formatStyle = FormatStyle.ARGUMENT)
    public void handleResources(@UriVariable final String path, final WebScriptResponse response) throws IOException {
        handleResource(path, response);
    }

    @Override
    protected String getBundleEntryPath(final String path) {
        return String.format("%s/%s", packagePath, path);
    }
}
```

So say that your resources are in `src/main/resources/my-extension/js/*`, the example above would serve those resources at 
`/alfresco/service/my-extension/js/*`
(on condition that `Resources` is located in the `my-extension` package)

The `handleResources` method is a good place to setup custom caching strategies.
