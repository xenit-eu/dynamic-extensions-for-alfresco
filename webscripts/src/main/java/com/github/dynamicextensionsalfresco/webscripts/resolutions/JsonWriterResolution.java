package com.github.dynamicextensionsalfresco.webscripts.resolutions;

import com.github.dynamicextensionsalfresco.webscripts.resolutions.internal.JsonWriterFactoryImpl;
import org.json.JSONException;
import org.json.JSONWriter;

/**
 * @author Laurent Van der Linden
 */
public abstract class JsonWriterResolution extends AbstractJsonResolution {
    @Override
    public void resolve() throws Exception {
        final JSONWriter jsonWriter = JsonWriterFactoryImpl.getJsonWriter(getResponse().getWriter());
        writeJson(jsonWriter);
    }

    protected abstract void writeJson(JSONWriter jsonWriter) throws JSONException;
}
