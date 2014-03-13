package com.github.dynamicextensionsalfresco.webscripts.resolutions;

import com.github.dynamicextensionsalfresco.webscripts.AnnotationWebScriptRequest;
import com.github.dynamicextensionsalfresco.webscripts.AnnotationWebscriptResponse;

/**
 * @author Laurent Van der Linden
 */
public interface Resolution {
    void resolve(final AnnotationWebScriptRequest request, final AnnotationWebscriptResponse response,
                 final ResolutionParameters params) throws Exception;
}
