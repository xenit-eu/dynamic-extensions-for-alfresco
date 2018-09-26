package com.github.dynamicextensionsalfresco.webscripts.resolutions;

import org.json.JSONException;
import org.json.JSONWriter;

/**
 * @author Laurent Van der Linden
 */
public abstract class JsonWriterResolution extends AbstractJsonResolution {
    @Override
    public void resolve() throws Exception {
        final JSONWriter jsonWriter = new JSONWriter(getResponse().getWriter());
        writeJson(jsonWriter);
    }

    protected abstract void writeJson(JSONWriter jsonWriter) throws JSONException;
}
