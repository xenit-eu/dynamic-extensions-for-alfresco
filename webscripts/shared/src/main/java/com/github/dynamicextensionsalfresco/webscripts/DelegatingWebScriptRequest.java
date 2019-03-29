package com.github.dynamicextensionsalfresco.webscripts;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.webscripts.Description.FormatStyle;
import org.springframework.extensions.webscripts.Match;
import org.springframework.extensions.webscripts.Runtime;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WrappingWebScriptRequest;

abstract class DelegatingWebScriptRequest implements WrappingWebScriptRequest {

    private final WebScriptRequest delegate;

    private final Map<String, Object> model = new LinkedHashMap<>();
    private Throwable thrownException = null;

    DelegatingWebScriptRequest(WebScriptRequest delegate) {

        this.delegate = delegate;
    }

    @Override
    public WebScriptRequest getNext() {
        return this.delegate;
    }

    @Override
    public String toString() {
        return this.delegate.toString();
    }

    @Override
    public Match getServiceMatch() {
        return this.delegate.getServiceMatch();
    }

    @Override
    public String getServerPath() {
        return this.delegate.getServerPath();
    }

    @Override
    public String getContextPath() {
        return this.delegate.getContextPath();
    }

    @Override
    public String getServiceContextPath() {
        return this.delegate.getServiceContextPath();
    }

    @Override
    public String getServicePath() {
        return this.delegate.getServicePath();
    }

    @Override
    public String getURL() {
        return this.delegate.getURL();
    }

    @Override
    public String getPathInfo() {
        return this.delegate.getPathInfo();
    }

    @Override
    public String getQueryString() {
        return this.delegate.getQueryString();
    }

    @Override
    public String[] getParameterNames() {
        return this.delegate.getParameterNames();
    }

    @Override
    public String getParameter(String name) {
        return this.delegate.getParameter(name);
    }

    @Override
    public String[] getParameterValues(String name) {
        return this.delegate.getParameterValues(name);
    }

    @Override
    public String[] getHeaderNames() {
        return this.delegate.getHeaderNames();
    }

    @Override
    public String getHeader(String name) {
        return this.delegate.getHeader(name);
    }

    @Override
    public String[] getHeaderValues(String name) {
        return this.delegate.getHeaderValues(name);
    }

    @Override
    public String getExtensionPath() {
        return this.delegate.getExtensionPath();
    }

    @Override
    public String getContentType() {
        return this.delegate.getContentType();
    }

    @Override
    public Content getContent() {
        return this.delegate.getContent();
    }

    @Override
    public Object parseContent() {
        return this.delegate.parseContent();
    }

    @Override
    public boolean isGuest() {
        return this.delegate.isGuest();
    }

    @Override
    public String getFormat() {
        return this.delegate.getFormat();
    }

    @Override
    public FormatStyle getFormatStyle() {
        return this.delegate.getFormatStyle();
    }

    @Override
    public String getAgent() {
        return this.delegate.getAgent();
    }

    @Override
    public String getJSONCallback() {
        return this.delegate.getJSONCallback();
    }

    @Override
    public boolean forceSuccessStatus() {
        return this.delegate.forceSuccessStatus();
    }

    @Override
    public Runtime getRuntime() {
        return this.delegate.getRuntime();
    }

}
