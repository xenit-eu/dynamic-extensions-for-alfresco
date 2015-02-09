package com.github.dynamicextensionsalfresco.webscripts.resolutions;

import com.github.dynamicextensionsalfresco.webscripts.AnnotationWebScriptRequest;
import com.github.dynamicextensionsalfresco.webscripts.AnnotationWebscriptResponse;
import org.springframework.extensions.webscripts.Description;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Laurent Van der Linden
 */
public abstract class AbstractResolution implements Resolution {
    private AnnotationWebScriptRequest request;
    private AnnotationWebscriptResponse response;
    private ResolutionParameters params;

    @Override
    public void resolve(AnnotationWebScriptRequest request, AnnotationWebscriptResponse response, ResolutionParameters params) throws Exception {
        this.request = request;
        this.response = response;
        this.params = params;

        resolve();
    }

    protected void addCacheControlHeaders(final WebScriptResponse response, ResolutionParameters params) {
        final Description.RequiredCache requiredCache = params.getDescription().getRequiredCache();
        if (requiredCache != null) {
            final List<String> cacheValues = new ArrayList<String>(3);
            if (requiredCache.getNeverCache()) {
                cacheValues.add("no-cache");
                cacheValues.add("no-store");
            }
            if (requiredCache.getMustRevalidate()) {
                cacheValues.add("must-revalidate");
            }
            if (cacheValues.isEmpty() == false) {
                response.setHeader("Cache-Control", StringUtils.collectionToDelimitedString(cacheValues, ", "));
            }
        }
    }

    protected AnnotationWebScriptRequest getRequest() {
        return request;
    }

    protected AnnotationWebscriptResponse getResponse() {
        return response;
    }

    protected ResolutionParameters getParams() {
        return params;
    }

    public abstract void resolve() throws Exception;
}
