package com.github.dynamicextensionsalfresco.gradle.internal.rest;

import java.io.File;

public class RestClientPostFileOptions extends RestClientOptions {
    private File file;


    public RestClientPostFileOptions(RestClientOptions options, File file) {
        super(options);
        this.file = file;
    }

    public File getFile() {
        return file;
    }
}
