package com.github.dynamicextensionsalfresco.webscripts.resolutions;

import org.springframework.extensions.webscripts.Description;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Laurent Van der Linden
 */
public abstract class AbstractResolution implements Resolution {
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
}
