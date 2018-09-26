package com.github.dynamicextensionsalfresco.webscripts.arguments;

import org.alfresco.repo.content.MimetypeMap;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import java.io.IOException;

/**
 * Allows adding optional JSONObject parameters to Uri handler methods.
 *
 * @author Laurent Van der Linden
 */
public class JsonObjectArgumentResolver extends AbstractTypeBasedArgumentResolver<JSONObject> {
    @Override
    protected Class<?> getExpectedArgumentType() {
        return JSONObject.class;
    }

    @Override
    protected JSONObject resolveArgument(WebScriptRequest request, WebScriptResponse response) {
        if (request.getContentType().startsWith(MimetypeMap.MIMETYPE_JSON)) {
            try {
                final String contentText = request.getContent().getContent();
                if (contentText != null) {
                    try {
                        return new JSONObject(contentText);
                    } catch (JSONException e) {
                        throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Failed to parse JSON body: " + contentText, e);
                    }
                }
            } catch (IOException ex) {
                throw new WebScriptException(Status.STATUS_BAD_REQUEST, "Failed to read body content", ex);
            }
        }
        return null;
    }
}