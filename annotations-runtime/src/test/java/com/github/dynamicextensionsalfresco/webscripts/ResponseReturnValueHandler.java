package com.github.dynamicextensionsalfresco.webscripts;

import com.github.dynamicextensionsalfresco.spring.Spied;
import com.github.dynamicextensionsalfresco.webscripts.annotations.Header;
import com.github.dynamicextensionsalfresco.webscripts.annotations.RequestParam;
import com.github.dynamicextensionsalfresco.webscripts.annotations.Uri;
import org.springframework.stereotype.Component;

@Component
@Spied
public class ResponseReturnValueHandler {

    @Uri("/handleResponse")
    public Person handleResponse(@RequestParam final String firstName, @RequestParam final String lastName) {
        return new Person(firstName, lastName);
    }

    @Uri("/handleXmlResponse")
    public PersonXml handleXmlResponse(@RequestParam final String firstName, @RequestParam final String lastName) {
        return new PersonXml(firstName, lastName);
    }
}
