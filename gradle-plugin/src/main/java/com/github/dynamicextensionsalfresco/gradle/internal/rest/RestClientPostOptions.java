package com.github.dynamicextensionsalfresco.gradle.internal.rest;

import java.io.InputStream;

public class RestClientPostOptions extends RestClientOptions {

    public RestClientPostOptions(RestClientOptions options, InputStream body) {
        super(options);
        this.body = body;
    }

    public InputStream getBody() {
        return body;
    }

    InputStream body;
}
