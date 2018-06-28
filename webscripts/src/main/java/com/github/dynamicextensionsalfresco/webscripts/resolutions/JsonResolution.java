package com.github.dynamicextensionsalfresco.webscripts.resolutions;

import com.github.dynamicextensionsalfresco.webscripts.AnnotationWebScriptRequest;
import com.github.dynamicextensionsalfresco.webscripts.AnnotationWebscriptResponse;
import org.alfresco.repo.content.MimetypeMap;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Laurent Van der Linden
 * @deprecated extend AbstractJsonResolution instead
 */
public abstract class JsonResolution implements Resolution {
    public static final String UTF_8 = "UTF-8";

    private int status = HttpServletResponse.SC_OK;

    protected JsonResolution() {
    }

    protected JsonResolution(int status) {
        this.status = status;
    }

    @Override
    public void resolve(AnnotationWebScriptRequest request, AnnotationWebscriptResponse response,
                        ResolutionParameters params) throws Exception {
        response.setContentType(MimetypeMap.MIMETYPE_JSON);
        response.setContentEncoding(UTF_8);
        response.setHeader("Cache-Control", "no-cache,no-store");
        response.setStatus(status);
    }
}
