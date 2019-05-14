package com.github.dynamicextensionsalfresco.gradle.internal.rest;

import com.github.dynamicextensionsalfresco.gradle.configuration.Authentication;

public class RestClientOptions {

    public String getPath() {
        return path;
    }

    public String getMimeType() {
        return mimeType;
    }

    public Authentication getAuthentication() {
        return authentication;
    }

    public RestClientOptions(String path, String mimeType) {
        this(path, mimeType, null);
    }

    public RestClientOptions(String path, String mimeType,
            Authentication authentication) {
        this.path = path;
        this.mimeType = mimeType;
        this.authentication = authentication;
    }

    public RestClientOptions(RestClientOptions options) {
        path = options.path;
        mimeType = options.mimeType;
        authentication = options.authentication;
    }

    String path;
    String mimeType;
    Authentication authentication;
}
