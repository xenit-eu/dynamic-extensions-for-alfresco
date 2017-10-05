package com.github.dynamicextensionsalfresco.webscripts;

import com.github.dynamicextensionsalfresco.spring.Spied;
import com.github.dynamicextensionsalfresco.webscripts.annotations.HttpMethod;
import com.github.dynamicextensionsalfresco.webscripts.annotations.Uri;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

@Component
@Spied
public class RequestBodyHandler {

    @Uri(value = "/requestbody/required", method = HttpMethod.POST, defaultFormat = MediaType.APPLICATION_JSON_VALUE)
    public PersonXml requiredBody(@RequestBody(required = true) PersonXml person) {
        return person;
    }

    @Uri(value = "/requestbody/notRequired", method = HttpMethod.POST, defaultFormat = MediaType.APPLICATION_JSON_VALUE)
    public PersonXml notRequired(@RequestBody(required = false) PersonXml person) {
        return person;
    }
}
