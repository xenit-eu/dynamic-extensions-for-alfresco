package com.github.dynamicextensionsalfresco.webscripts.messages;

import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class AnnotationWebScriptInputMessage implements HttpInputMessage {

    private WebScriptRequest request;

    private final HttpHeadersWrapper headers;

    public AnnotationWebScriptInputMessage(WebScriptRequest request) {
        this.request = request;

        this.headers = new HttpHeadersWrapper(request);
    }

    @Override
    public InputStream getBody() throws IOException {
        return request.getContent().getInputStream();
    }

    @Override
    public HttpHeaders getHeaders() {
        return this.headers;
    }

    class HttpHeadersWrapper extends HttpHeaders {
        private final WebScriptRequest request;

        public HttpHeadersWrapper(WebScriptRequest request) {
            this.request = request;
        }

        @Override
        public List<String> get(Object key) {
            String[] values = request.getHeaderValues(key.toString());

            return Arrays.asList(values);
        }

        @Override
        public String getFirst(String headerName) {
            String[] values = request.getHeaderValues(headerName);

            if (values == null){
                return null;
            } else {
                return values[0];
            }
        }
    }
}
