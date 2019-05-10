package com.github.dynamicextensionsalfresco.gradle.internal.rest;

import com.github.dynamicextensionsalfresco.gradle.configuration.Authentication;
import com.github.dynamicextensionsalfresco.gradle.configuration.Endpoint;
import com.github.dynamicextensionsalfresco.gradle.internal.rest.RestClientException.RestClientStatus;
import groovy.json.JsonSlurper;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import org.apache.commons.io.IOUtils;

/**
 * Client for performing REST operations on the Alfresco service layer.
 * <p>
 * This implementation relies on java.net classes.
 */
public class RestClient {
    private final Endpoint endpoint;
    private final Authentication authentication;


    public RestClient(Endpoint endpoint, Authentication authentication) {
        this.endpoint = endpoint;
        this.authentication = authentication;
    }


    public Object postFile(RestClientPostFileOptions options) throws IOException {
        try(InputStream fileInputStream = new FileInputStream(options.getFile())) {
            RestClientPostOptions postOptions = new RestClientPostOptions(options, fileInputStream);
            return post(postOptions);
        }
    }

    public Object post(RestClientPostOptions options) throws IOException {
        return connect(options, "POST", conn -> {
            conn.setDoOutput(true);

            if(options.body != null) {
                IOUtils.copy(options.body, conn.getOutputStream());

            }
            conn.getOutputStream().flush();
        });
    }

    public Object get(RestClientOptions options) throws IOException {
        return connect(options, "GET", c -> {});
    }

    private Object connect(RestClientOptions options, String method, ThrowingConsumer<HttpURLConnection, IOException> onConnect)
            throws IOException {
        if (options.path.isEmpty()) {
            throw new NullPointerException("A url path should be provided");
        }

        HttpURLConnection conn = (HttpURLConnection) new URL(
                endpoint.getUrl().get() +"/"+options.path.replaceAll("^\\/", "")).openConnection();
        conn.setRequestMethod(method);
        Authentication authentication = options.authentication == null?this.authentication:options.authentication;

        if(authentication != null) {
            conn.setRequestProperty("Authorization", "Basic "+authentication.getBasic().get());
        }

        if(options.mimeType != null) {
            conn.setRequestProperty("Content-Type", options.mimeType);
        }

        try {
            if (onConnect != null) {
                onConnect.throwingAccept(conn);
            }

            return new JsonSlurper().parseText((String)conn.getContent());
        } catch (Exception e) {
            String message = e.getMessage();
            String errorText = IOUtils.toString(conn.getErrorStream());
            if (errorText != null) {
                message = errorText;
            }
            String contentType = conn.getHeaderField("Content-Type");
            if(contentType != null && contentType.contains("application/json")) {
                Map<String, Object> json = (Map<String, Object>) new JsonSlurper().parseText(errorText);
                if (json.containsKey("message")) {
                    message = (String) json.get("message");
                }
            }
            throw new RestClientException(new RestClientStatus(conn.getResponseCode(), conn.getResponseMessage()), message);
        }
    }
}

