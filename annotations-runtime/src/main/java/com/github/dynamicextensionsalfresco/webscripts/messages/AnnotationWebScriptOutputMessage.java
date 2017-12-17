package com.github.dynamicextensionsalfresco.webscripts.messages;

import com.github.dynamicextensionsalfresco.webscripts.AnnotationWebscriptResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpResponse;

import java.io.IOException;
import java.io.OutputStream;

public class AnnotationWebScriptOutputMessage implements ServerHttpResponse {

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

    @Override
    public void setStatusCode(HttpStatus status) {
        response.setStatus(status.value());
    }

    @Override
    public void close() {
        // TODO Should we do something here? I don't think so?
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
