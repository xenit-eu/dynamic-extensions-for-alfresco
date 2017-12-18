package com.github.dynamicextensionsalfresco.webscripts;


import com.github.dynamicextensionsalfresco.spring.Spied;
import com.github.dynamicextensionsalfresco.webscripts.annotations.RequestParam;
import com.github.dynamicextensionsalfresco.webscripts.annotations.Uri;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Component
@Spied
public class HttpEntityReturnValueHandler {

    static final String HEADER_TEST_KEY = "Xnanana";
    static final String HEADER_TEST_VALUE_1 = "Batcache";
    static final String HEADER_TEST_VALUE_2 = "TestValue2";
    static final String HEADER_TEST_KEY_BIS = "XtestHeaderKey";
    static final String HEADER_TEST_VALUE_BIS = "BisTestValue1";

    @Uri(value = "/handleHttpEntityResponseWithHeaders")
    public HttpEntity<Void> handleResponseWithHeaders() {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(HEADER_TEST_KEY, HEADER_TEST_VALUE_1);
        headers.add(HEADER_TEST_KEY, HEADER_TEST_VALUE_2);
        headers.add(HEADER_TEST_KEY_BIS, HEADER_TEST_VALUE_BIS);
        return new HttpEntity<>(headers);
    }

    @Uri(value = "/handleResponseEntityResponseWithStatusCode")
    public ResponseEntity<Void> handleResponseWithStatus() {
        return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);
    }

    @Uri("/handleHttpEntityResponseWithBody")
    public HttpEntity<Person> handleResponseWithBody(@RequestParam final String firstName, @RequestParam final String lastName) {
        return new HttpEntity<>(new Person(firstName, lastName));
    }

    @Uri("/handleHttpEntityResponseWithXmlBody")
    public HttpEntity<PersonXml> handleXmlResponseBody(@RequestParam final String firstName, @RequestParam final String lastName) {
        return new HttpEntity<>(new PersonXml(firstName, lastName));
    }
}
