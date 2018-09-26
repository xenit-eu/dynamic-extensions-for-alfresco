package com.github.dynamicextensionsalfresco.webscripts.resolutions;

import com.github.dynamicextensionsalfresco.webscripts.AnnotationWebScriptRequest;
import com.github.dynamicextensionsalfresco.webscripts.AnnotationWebscriptResponse;
import org.alfresco.repo.content.MimetypeMap;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Laurent Van der Linden
 */
public abstract class AbstractJsonResolution extends AbstractResolution {
    public static final String UTF_8 = "UTF-8";

    private Integer status = null;

    protected AbstractJsonResolution() {
    }

    /**
     * @deprecated use {@link #withStatus(int)} instead
     *
     * @param status http status code
     */
    protected AbstractJsonResolution(int status) {
        this.status = status;
    }

    @Override
    public void resolve(@Nonnull AnnotationWebScriptRequest request,
                        @Nonnull AnnotationWebscriptResponse response,
                        @Nonnull ResolutionParameters params) throws Exception {
        super.resolve(request, response, params);
        response.setContentType(MimetypeMap.MIMETYPE_JSON);
        response.setContentEncoding(UTF_8);
        response.setHeader("Cache-Control", "no-cache,no-store");

        if (status != null) {
            response.setStatus(status);
        }
    }
}
