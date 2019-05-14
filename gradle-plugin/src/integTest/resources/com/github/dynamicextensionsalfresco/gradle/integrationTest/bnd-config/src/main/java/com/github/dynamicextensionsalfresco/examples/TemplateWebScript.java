package com.github.dynamicextensionsalfresco.examples;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.github.dynamicextensionsalfresco.webscripts.annotations.HttpMethod;
import com.github.dynamicextensionsalfresco.webscripts.annotations.RequestParam;
import com.github.dynamicextensionsalfresco.webscripts.annotations.Uri;
import com.github.dynamicextensionsalfresco.webscripts.annotations.WebScript;

import org.alfresco.service.cmr.repository.TemplateProcessor;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.TemplateProcessorRegistry;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.stereotype.Component;

/**
 * Illustrates the use of Freemarker templates with annotation-based Web Scripts, using the
 * convention-over-configuration approach. (<strong>Note</strong>: this approach is new in Dynamic Extensions 1.0
 * milestone 4. When this class is used with earlier milestones, the Web Script generates an empty response.)
 * <p>
 * Web Script handler methods that generate their responses using a template should return a {@link Map}, which serves
 * as the model that is fed to the template. This approach is virtually identical to how {@link DeclarativeWebScript}
 * works with models and templates.
 * <p>
 * The path to the template is calculated as follows:<blockquote>
 * <code>path/to/package/&lt;Java class name&gt;.&lt;Java method name&gt;.&lt;HTTP method&gt;.&lt;format&gt;.ftl</code>
 * </blockquote>
 * <p>
 * The implementation loads Freemarker FTL templates from OSGi bundle entries. In the context of creating Dynamic
 * Extensions, OSGi bundle entries can be considered to be equivalent to the classpath resources in the OSGi bundle JAR.
 * <p>
 * The implementation currently has one limitation: Freemarker templates do not have access to <a
 * href="http://wiki.alfresco.com/wiki/Template_Guide#Template_Models">utility objects in the template model</a>, such
 * as <code>person</code> and <code>companyhome</code>. One could argue that using these utility objects is equivalent
 * to putting controller logic in templates. Generally speaking, mixing controller and template logic is not a good
 * practice.
 * <p>
 * To avoid breaking backwards compatibility with releases prior to milestone 4, returning a {@link Map} from a handler
 * method will most likely be the only approach supported by Dynamic Extensions for rendering templates using
 * convention-over-configuration.
 * <p>
 * An alternative to rendering templates using this approach, is to work with {@link TemplateProcessorRegistry} and
 * {@link TemplateProcessor} programmatically.
 *
 * @author Laurens Fridael
 */
@Component
@WebScript
public class TemplateWebScript {

    /**
     * This particular Web Script URI endpoint is configured to have a default format of <code>html</code>. In general,
     * it is highly recommended to configure a default format, if only to relieve the client from having to specify a
     * format explicitly.
     * <p>
     * The default template for this handler method should be located here: <blockquote>
     * <code>nl/runnable/alfresco/examples/HelloWebScript.handleHelloTemplate.get.html.ftl</code> </blockquote>
     * <p>
     * Per the Web Scripts specification, the client can specify the output format using the <code>format</code> request
     * parameter or as an extension of the URL.
     *
     * @param name
     * @param response
     * @return
     * @throws IOException
     */
    @Uri(method = HttpMethod.GET, value = "/dynamic-extensions/examples/hello-template", defaultFormat = "html")
    public Map<String, Object> handleHelloTemplate(@RequestParam final String name, final WebScriptResponse response)
            throws IOException {
        final Map<String, Object> model = new HashMap<String, Object>();
        model.put("name", name);
        return model;
    }

}
