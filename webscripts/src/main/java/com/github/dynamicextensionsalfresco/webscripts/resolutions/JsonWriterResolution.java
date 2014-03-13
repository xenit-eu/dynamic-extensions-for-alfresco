package com.github.dynamicextensionsalfresco.webscripts.resolutions;

import com.github.dynamicextensionsalfresco.webscripts.AnnotationWebScriptRequest;
import com.github.dynamicextensionsalfresco.webscripts.AnnotationWebscriptResponse;
import org.json.JSONException;
import org.json.JSONWriter;

/**
 * @author Laurent Van der Linden
 */
public abstract class JsonWriterResolution extends JsonResolution {
    @Override
    public void resolve(AnnotationWebScriptRequest request, AnnotationWebscriptResponse response, ResolutionParameters params) throws Exception {
        super.resolve(request, response, params);
        final JSONWriter jsonWriter = new JSONWriter(response.getWriter());
        writeJson(jsonWriter);
    }

    protected abstract void writeJson(JSONWriter jsonWriter) throws JSONException;
}
