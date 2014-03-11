package com.github.dynamicextensionsalfresco.webscripts;

import com.github.dynamicextensionsalfresco.webscripts.arguments.AbstractTypeBasedArgumentResolver;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.stereotype.Component;

/**
 * JsonObject resolver for test purposes
 *
 * @author Laurent Van der Linden
 */
@Component
public class JsonObjectArgumentResolver extends AbstractTypeBasedArgumentResolver<JSONObject> {
    @Override
    protected Class<?> getExpectedArgumentType() {
        return JSONObject.class;
    }

    @Override
    protected JSONObject resolveArgument(WebScriptRequest request, WebScriptResponse response) {
        try {
            return new JSONObject(request.getContent().getContent());
        } catch (Exception e) {
            return null;
        }
    }
}
