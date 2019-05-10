package com.github.dynamicextensionsalfresco.gradle.internal;

import aQute.bnd.gradle.BundleTaskConvention;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.gradle.api.Task;

public class BndHandler {
    private BundleTaskConvention bundleTaskConvention;
    private Task task;
    private Map<String, String> initializedInstructions;

    public BndHandler(Task task, BundleTaskConvention bundleTaskConvention) {
        this.task = task;
        this.bundleTaskConvention = bundleTaskConvention;
        initializedInstructions = null;
    }


    public Map<String, String> getHeaders() {
        if(initializedInstructions == null) {
            Properties properties = new Properties();
            try {
                if(bundleTaskConvention.getBndfile() != null && bundleTaskConvention.getBndfile().isFile()) {
                    try(InputStream bndFileInputStream = new FileInputStream(bundleTaskConvention.getBndfile())) {
                        properties.load(bndFileInputStream);
                    }
                } else {
                    properties.load(new StringReader(bundleTaskConvention.getBnd()));
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
            initializedInstructions = new HashMap<>();
            properties.forEach((k, v) -> initializedInstructions.put((String) k, (String) v));
        }
        return initializedInstructions;
    }

    public boolean hasHeader(String header) {
        return getHeaders().containsKey(header);
    }

    public void setHeader(String header, String value) {
        Map<String, String> headers = getHeaders();
        headers.put(header, value);
        bundleTaskConvention.bnd(headers);
        // Clear BND file because we have added headers inline which we want to prefer over the file configuration
        // We can not call setBndfile(null) because it calls project.file() underneath. So we reach in with reflection
        if(bundleTaskConvention.getBndfile() != null) {
            try {
                Field bndfileField = bundleTaskConvention.getClass().getDeclaredField("bndfile");
                bndfileField.setAccessible(true);
                bndfileField.set(bundleTaskConvention, null);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new IllegalStateException("Error configuring OSGi headers for task "+task.getName()+": configuring BND with bndfile is not supported.");
            }
        }
    }
}
