package com.github.dynamicextensionsalfresco.webscripts;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;

import java.io.IOException;
import java.io.OutputStream;

public class AnnotationWebScriptOutputMessage implements HttpOutputMessage {

    private AnnotationWebScriptRequest request;
    private AnnotationWebscriptResponse response;


    public AnnotationWebScriptOutputMessage(AnnotationWebScriptRequest request, AnnotationWebscriptResponse response) {
        this.request = request;
        this.response = response;
    }

    @Override
    public OutputStream getBody() throws IOException {
        return response.getOutputStream();
    }

    @Override
    public HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();



        return headers;
    }
}
