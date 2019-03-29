package com.github.dynamicextensionsalfresco.webscripts;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WrappingWebScriptRequest;

public class AnnotationWebScriptRequest extends DelegatingWebScriptRequest implements WrappingWebScriptRequest {

    private final Map<String, Object> model = new LinkedHashMap<>();
    private Throwable thrownException = null;

    public AnnotationWebScriptRequest(WebScriptRequest request) {
        super(request);

    }

    public WebScriptRequest getWebScriptRequest() {
        return this.getNext();
    }


    public Map<String, Object> getModel() {
        return model;
    }

    public Throwable getThrownException() {
        return thrownException;
    }

    public void setThrownException(Throwable thrownException) {
        this.thrownException = thrownException;
    }

}

