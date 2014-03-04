package com.github.dynamicextensionsalfresco.webscripts.resolutions;

import com.github.dynamicextensionsalfresco.webscripts.AnnotationWebScriptRequest;
import com.github.dynamicextensionsalfresco.webscripts.AnnotationWebscriptResponse;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Laurent Van der Linden
 */
public abstract class JsonResolution implements Resolution {
    public static final String APPLICATION_JSON = "application/json";
    public static final String UTF_8 = "utf-8";

    private int status = HttpServletResponse.SC_OK;

    protected JsonResolution() {
    }

    protected JsonResolution(int status) {
        this.status = status;
    }

    @Override
    public void resolve(AnnotationWebScriptRequest request, AnnotationWebscriptResponse response,
                        ResolutionParameters params) throws Exception {
        response.setContentType(APPLICATION_JSON);
        response.setContentEncoding(UTF_8);
        response.setHeader("Cache-Control", "no-cache");
        response.setStatus(status);
    }
}
