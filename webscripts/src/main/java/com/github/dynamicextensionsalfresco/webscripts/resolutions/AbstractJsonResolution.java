package com.github.dynamicextensionsalfresco.webscripts.resolutions;

import com.github.dynamicextensionsalfresco.webscripts.AnnotationWebScriptRequest;
import com.github.dynamicextensionsalfresco.webscripts.AnnotationWebscriptResponse;
import org.alfresco.repo.content.MimetypeMap;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Laurent Van der Linden
 */
public abstract class AbstractJsonResolution extends AbstractResolution {
    public static final String UTF_8 = "UTF-8";

    private int status = HttpServletResponse.SC_OK;

    protected AbstractJsonResolution() {
    }

    protected AbstractJsonResolution(int status) {
        this.status = status;
    }

    @Override
    public void resolve(AnnotationWebScriptRequest request, AnnotationWebscriptResponse response,
                        ResolutionParameters params) throws Exception {
        super.resolve(request, response, params);
        response.setContentType(MimetypeMap.MIMETYPE_JSON);
        response.setContentEncoding(UTF_8);
        response.setHeader("Cache-Control", "no-cache");
        response.setStatus(status);
    }
}
