package com.github.dynamicextensionsalfresco.webscripts.resolutions;

import com.github.dynamicextensionsalfresco.webscripts.AnnotationWebScriptRequest;
import com.github.dynamicextensionsalfresco.webscripts.AnnotationWebscriptResponse;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Laurent Van der Linden
 */
public class RedirectResolution implements Resolution {
    private String path;

    public RedirectResolution(String path) {
        this.path = path;
    }

    @Override
    public void resolve(@Nonnull AnnotationWebScriptRequest request,
                        @Nonnull AnnotationWebscriptResponse response,
                        @Nonnull ResolutionParameters params) throws IOException {
        Assert.hasText(path);
        if (path.startsWith("/") == false) {
            path = "/" + path;
        }
        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY); // 302
        response.setHeader("Location", request.getServiceContextPath() + path);
    }

    public String getPath() {
        return path;
    }
}
