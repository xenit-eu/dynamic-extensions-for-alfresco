package com.github.dynamicextensionsalfresco.webscripts.messages;

import com.github.dynamicextensionsalfresco.webscripts.AnnotationWebscriptResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
    }
    
    @SuppressWarnings("unused")
    public void flush() {
        
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

        @Override
        public void putAll(Map<? extends String, ? extends List<String>> map) {
            super.putAll(map);

            // Should be supported to putAll headers of an HttpEntity.
            for (Entry<? extends String, ? extends List<String>> entry : map.entrySet()) {
                if (entry.getValue() == null || entry.getValue().isEmpty()) {
                    continue;
                }
                response.setHeader(entry.getKey(), entry.getValue().get(0));
                for (int i = 1; i < entry.getValue().size(); i++) {
                    response.addHeader(entry.getKey(), entry.getValue().get(i));
                }
            }
        }
    }

}
