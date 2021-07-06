package com.github.dynamicextensionsalfresco.webscripts.resolutions.internal;

import java.io.Writer;
import org.json.JSONWriter;

public class JsonWriterFactoryImpl {

    private JsonWriterFactoryImpl() {}

    public static JSONWriter getJsonWriter(Writer writer) {
        return new JSONWriter(writer);
    }
}
