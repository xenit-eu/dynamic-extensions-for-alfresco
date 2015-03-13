package com.github.dynamicextensionsalfresco.webscripts.resolutions;

import com.github.dynamicextensionsalfresco.webscripts.AnnotationWebScriptRequest;
import com.github.dynamicextensionsalfresco.webscripts.AnnotationWebscriptResponse;
import org.springframework.extensions.webscripts.Description;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.util.StringUtils;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Template for other Resolution implementations
 *
 * @author Laurent Van der Linden
 */
public abstract class AbstractResolution implements Resolution {
    private AnnotationWebScriptRequest request;
    private AnnotationWebscriptResponse response;
    private ResolutionParameters params;

    private String encoding;
    private String contentType;
    private int statusCode = HttpServletResponse.SC_OK;

    public void resolve(@Nonnull AnnotationWebScriptRequest request,
                        @Nonnull AnnotationWebscriptResponse response,
                        @Nonnull ResolutionParameters params) throws Exception {
        this.request = request;
        this.response = response;
        this.params = params;

        if (this.contentType != null) {
            response.setContentType(this.contentType);
        }

        if (this.encoding != null) {
            response.setContentEncoding(this.encoding);
        }

        if (this.statusCode != HttpServletResponse.SC_OK) {
            response.setStatus(this.statusCode);
        }

        resolve();
    }

    public AbstractResolution withContentType(@Nonnull final String contentType) {
        this.contentType = contentType;
        return this;
    }

    public AbstractResolution withEncoding(@Nonnull final String encoding) {
        this.encoding = encoding;
        return this;
    }

    public AbstractResolution withHeader(@Nonnull final String headerName, @Nonnull final String headerValue) {
        // we set these directly as they are unlikely to be overruled by subclasses
        getResponse().setHeader(headerName, headerValue);
        return this;
    }

    public AbstractResolution withStatus(final int statusCode) {
        this.statusCode = statusCode;
        return this;
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

    protected Writer getWriter() throws IOException {
        return getResponse().getWriter();
    }

    protected ResolutionParameters getParams() {
        return params;
    }

    public abstract void resolve() throws Exception;
}
