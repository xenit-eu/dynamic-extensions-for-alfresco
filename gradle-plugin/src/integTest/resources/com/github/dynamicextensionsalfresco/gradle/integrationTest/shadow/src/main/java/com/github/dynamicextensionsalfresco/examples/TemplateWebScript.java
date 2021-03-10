package com.github.dynamicextensionsalfresco.examples;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.github.dynamicextensionsalfresco.webscripts.annotations.HttpMethod;
import com.github.dynamicextensionsalfresco.webscripts.annotations.RequestParam;
import com.github.dynamicextensionsalfresco.webscripts.annotations.Uri;
import com.github.dynamicextensionsalfresco.webscripts.annotations.WebScript;

import org.springframework.stereotype.Component;

@Component
@WebScript
public class TemplateWebScript {

    @Uri(method = HttpMethod.GET, value = "/dynamic-extensions/examples/hello-template", defaultFormat = "html")
    public Map<String, Object> handleHelloTemplate(@RequestParam final String name)
            throws IOException {
        final Map<String, Object> model = new HashMap<String, Object>();
        model.put("name", name);
        return model;
    }

}
