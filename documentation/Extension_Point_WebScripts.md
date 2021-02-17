# Dynamic Extensions annotation based WebScripts

## Basic webscript sample

```java
@Component
@WebScript
@Authentication(AuthenticationType.USER)
public class NodeScript {
    @Autowired NodeService nodeService;

    @Uri(value = "/nodename")
    public Object index(@RequestParam NodeRef node, WebScriptResponse response) {
        Serializable name = nodeService.getProperty(node, ContentModel.PROP_NAME);
        response.getWriter().append(name);
    }
}
```

* `@Component` registers our webscript as a Spring bean. This is the basic requirement for any webscript.
* `@WebScript` tags this instance as a webscript and allows you to set a description and family. (similar to the .desc.xml)
* `@Uri` defines individual webscript endpoints which by default are set to accept http GET calls.
* `@RequestParam` allows you bind http parameters (?noderef=workspace...) to uri method parameters. The parameter name is by default deduced via reflection (debug compile required). By default the parameter is required, you can make a parameter optional by using `@RequestParam(required = false)`. Supported data conversions are:
    * String
    * String[]
    * Long
    * Long[]
    * Integer
    * Integer[]
    * Boolean
    * Boolean[]
    * QName
    * NodeRef
* `@Authentication` can be used to indicate which type of authentication we should have to use this endpoint.

If you need either the WebScriptRequest or WebScriptResponse, simply add a parameter with the matching type. (like the response in the example)

## UriVariable

Similar to the `@RequestParam`, but binds a parameter to a url variable, not a parameter.

Used as `GET /alfresco/service/hello/you`.

```java
@Uri(“/hello/{name}”)
public void sayHello(@UriVariable String name, WebScriptResponse response) {
    response.getWriter().append(name);
}
```

## Attribute providers and injection

Attribute providers are based on Spring MVC. It allows you to register your own dependency providers for method parameters.
If you often need a state-full object inside your uri handling methods, you can create an Attribute prodiver method for it.
These provider methods can in turn receive dependencies via their method parameters.

This example shows how you can define a reusable JSONOutput provider for all controller classes that extend an abstract class.

Other examples might be request or response helper classes that offer convenience methods on top of the request or response.

The `@Attribute` annotation is used in 2 ways:
* mark a method parameter that should be provided by an Attribute provider (receiver)
* mark a method that is capable of providing parameters (provider)

```java
public abstract class MyAbstractController {
    @Attribute
    protected JSONOutput jsonOutput(final WebScriptResponse response) throws IOException {
        return new JSONOutput(response);
    }

    protected class JSONOutput {
        private final WebScriptResponse response;

        public JSONOutput(WebScriptResponse response) {
            this.response = response;
        }

        public JSONWriter getWriter() throws IOException {
            response.setContentType("application/json");
            response.setContentEncoding("utf-8");
            response.setHeader("Cache-Control", "no-cache");
            return new JSONWriter(response.getWriter());
        }
    }
}
```

```java
public class TimeController extends MyAbstractController {
    @Uri(“/time”)
    public void sayHello(@Attribute JSONOutput jsonOutput) {
        JSONWriter json = jsonOutput.getWriter();
        json.object()
                .key("time").value(System.currentTimeMillis())
            .endObject();
    }
}
```

In addition, your can provide your own type based attribute resolvers.
Let's say we want to be able to add JsonObject to our Uri method arguments so that it would contain a JSON POST body:
```java
@Component
public class JsonObjectArgumentResolver extends AbstractTypeBasedArgumentResolver<JSONObject> {
    @Override
    protected Class<?> getExpectedArgumentType() {
        return JSONObject.class;
    }

    @Override
    protected JSONObject resolveArgument(WebScriptRequest request, WebScriptResponse response) {
        try {
            return new JSONObject(request.getContent().getContent());
        } catch (Exception e) {
            return null;
        }
    }
}
```

By annotating the resolver as a Spring bean (@Component), it becomes available to all webscripts in the same extension.

If your want the resolver to be available across all extensions, register it as a OSGi service. (@OsgiService)

## Multipart uploads

There are 2 ways to handle uploads:

Using `Content`
```java
@Uri(value = “/upload”, multipartProcessing = true)
public void uploadContent(Content content, @Header(“Content-Type”) String cType) {
    content.getInputStream();
}
```

or a regular `File`

```java
@Uri(value = “/upload”, multipartProcessing = true)
public void uploadContent(File file) {
    file.isFile();
}
```

## Templates

By returning a `Map` from your controller, the framework knows to look for a matching template.

```java
@Component @WebScript
public class Sample {
    @Uri(“/hello/{name}”)
    public Map<String,Object> sayHello(@UriVariable String name) {
        Map<String,Object> model = new HashMap<String,Object>();
        
        model.put("name", name);
        
        return model;
    }
}
```

The template is expected to reside in the same package and the name is constructed as follows:
`<class>.<method>.<httpmethod>.<format>.ftl` -> `Sample.sayHello.get.html.ftl`

If you do not provide a template with the correct name, the expected path + name will be printed in the output.

You can also define an explicit template path using `@ResponseTemplate`. With this annotation set on the method, a template will be used regardless of the return type. (Map no longer required)

As a final note, you can also inject the model:

```java
public void sayHello(@Model Map<String,Object> model) {
    model.put("name", name);
}
```

This can be very useful in combination with `@Before` handlers. (provide default model data)

## Error templates

When errors occur, you might want to have a special template in place to render the error or a useful error message for the user.

You can do this at several granularities:
* `<classname>.<method>.html.404.ftl`
* `<package>.html.404.ftl`
* `html.404.ftl`

## Exception handlers

If you want to reuse logic to handle exceptions across WebScript methods or classes, consider annotating a dedicated method with `@ExceptionHandler`:

```java
@ExceptionHandler(IllegalArgumentException.class)
protected void handleIllegalArgument(IllegalArgumentException exception, WebScriptResponse response) {
    response.setStatus(400);
}
```
This handler method must be defined in the same `@WebScript` annotated class that will throw the Exceptions to be handled.

**As of Alfresco 6**, it is also possible to separate out exception handler methods into their own class, 
using an interface with default methods. The interface can then be implemented by any `@WebScript` class that 
requires it:
```java
public interface ExceptionHandlers {
    @ExceptionHandler(IllegalArgumentException.class)
    default void handleIllegalArgument(IllegalArgumentException exception, WebScriptResponse response) {
        response.setStatus(400);
    }
}

@Component @Webscript
public class SampleWebscript implements ExceptionHandlers {
    @Uri(value = "/document")
    public void retrieveDocument(@RequestParam String name) {
        if (!isValid(name))
            throw new IllegalArgumentException();
    }
}
```

## Before

Any generic initialization can be put in WebScript initialization method, marked with `@Before`. (inherited from superclasses)

## Request and ResponseBody

Since version 1.7, there is support for the Spring [RequestBody](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/bind/annotation/RequestBody.html) and [ResponseBody](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/bind/annotation/ResponseBody.html) annotation. These work with using HttpMessageConverters. At the moment, there are only 2 HttpMessageConverterts available by default. The Jackson converter for json, and the JAXB converter for xml.

### RequestBody
The `@RequestBody` annotation will _resolve the method argument depending on the content type of the request._ When annotating a method parameter with this, the body of the request will be automatically resolved to the parameter object. Dynamic extensions will select a resolver based on the `Content-Type` header from the request. If no resolver for the specified content-type can be found, a `RuntimeException` will be thrown. 

```java
@Uri(value = "/example", method = HttpMethod.POST)
public void exampleRequestBody(@RequestBody MyObject thing) {
    // do some work
}
```

There is also support for the `required` parameter in the annotation. When `required = true`, an exception will be thrown of the body of the request is empty.

### ResonseBody
When annotating a webscript with the `@ResponseBody` annotation, the _method return value should be bound to the web response body_. This means that when your webscript returns an Object, that object should be serialized to a data format that the client accepts. This format has to be specified in the `Accept` header from the request. If no accept header is present, the webscript default format will be used.

```java
@Uri(value = "/getPerson", defaultFormat = MediaType.APPLICATION_JSON_VALUE)
@ResponseBody
public Person handleDefaultResponse(@RequestParam final String firstName, @RequestParam final String lastName) {
    return new Person(firstName, lastName);
}
```

Important note when using the `@ResponseBody` annotation. When the accept header requests `application/xml` you have to make sure that your pojo's are annotated corretly for JAXB to serialize them. Every pojo needs an `@XmlRootElement` annotation. Also, when your method return a list of objects, for example: `List<Person>`. JAXB will also fail to serialize the return value. See [this blogpost](https://howtodoinjava.com/jaxb/jaxb-exmaple-marshalling-and-unmarshalling-list-or-set-of-objects/) for a workaround.

## Http/Response Entity 
Since version 1.7.3 there is support for the spring [HttpEntity](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/http/HttpEntity.html) and [ResponseEntity](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/http/ResponseEntity.html) as return type. This makes it possible to set the status code and headers of the response.

```java
@Uri(value = "/getPerson", defaultFormat = MediaType.APPLICATION_JSON_VALUE)
public ResponseEntity<Person> handleDefaultResponse(@RequestParam final String firstName, @RequestParam final String lastName) {
    final Person ret = new Person(firstName, lastName);

    MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
    headers.add("Xnananana", "Batcache");

    return new ResponseEntity<Person>(ret, headers, HttpStatus.I_AM_A_TEAPOT);
}
```

## Resolutions

So far, the output of a webscript was either handled manually (using response argument) or by returning a model from the controller method.

In order to improve control flow and enable reuse of output strategies, you can use/implement resolutions.

All webscript Uri methods can return an implementation of `com.github.dynamicextensionsalfresco.webscripts.resolutions.Resolution`.

This is a simple interface, capable of handling a webrequest by itself:

```java
public interface Resolution {
    void resolve(final AnnotationWebScriptRequest request, final AnnotationWebscriptResponse response,
                 final ResolutionParameters params) throws Exception;
}
```

Whenever your return a model from your Uri method, you're implicitly returning a `TemplateResolution` from your method.

The power of a Resoluton comes with reuse:
if your webscript needs to output JSON, you can return an anonymous implementation of `JsonWriterResolution`:


```java
@Uri("/api/hello")
public Resolution sayHello() {
  return new JsonWriterResolution() {
    void writeJson(JSONWriter jsonWriter) throws JSONException {
      jsonWriter.object()
        .key("message").value("hello")
        .endObject();
    }
  };
}
```

If you want to use another Json serialisation library, create a new subclass of `JsonResolution`.

Because resolutions are returned, they automatically control the flow of your Uri method.
 

## Resetting the Webscript index

Dynamic extensions promises hot reloading of all changes, but for 2 scenarios, resetting the webscript index at `/alfresco/service/` will be required:

### Accessed url before webscript was deployed

The Alfresco webscript index keeps a cache that maps urls to a webscript implementation.

Even when no webscript can be found, a cache entry is created, so Alfresco knows not to keep looking for a webscript implementation, but can simply return `404`. (Alfresco assumes webscripts are not hot deployed)

This means that if you access say `/alfresco/service/helloworld` before any extension is deployed, that url will always result in a `404` until you reset the index.

### Updating the formatStyle

The formatStyle has an impact on uri parsing (EXTENSION style) and is stored as part of that strategy in the webscript index cache.

So if you deploy a webscript with uri `/resources/{path}` using formatStyle.EXTENSION (default) first and then correct the formatStyle, you also need to reset the index to get the correct behaviour.