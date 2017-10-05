package com.github.dynamicextensionsalfresco.webscripts.messages;

import com.github.dynamicextensionsalfresco.webscripts.AnnotationWebScriptRequest;
import com.github.dynamicextensionsalfresco.webscripts.AnnotationWebscriptResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AnnotationWebScriptOutputMessage implements HttpOutputMessage {

    private final AnnotationWebscriptResponse response;

    private final HttpHeadersWrapper headers;

    public AnnotationWebScriptOutputMessage(AnnotationWebscriptResponse response) {
        this.response = response;

        this.headers = new HttpHeadersWrapper(response);
    }

    @Override
    public OutputStream getBody() throws IOException {
        return response.getOutputStream();
    }

    @Override
    public HttpHeaders getHeaders() {
        return this.headers;
    }

    public class HttpHeadersWrapper extends HttpHeaders {
        private final AnnotationWebscriptResponse response;

        public HttpHeadersWrapper(AnnotationWebscriptResponse response) {
            this.response = response;
        }

        @Override
        public void add(String headerName, String headerValue) {
            super.add(headerName, headerValue);

            response.addHeader(headerName, headerValue);
        }

        @Override
        public void set(String headerName, String headerValue) {
            super.set(headerName, headerValue);

            response.setHeader(headerName, headerValue);
        }
    }

}
