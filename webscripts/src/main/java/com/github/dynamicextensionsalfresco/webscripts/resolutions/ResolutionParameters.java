package com.github.dynamicextensionsalfresco.webscripts.resolutions;

import org.springframework.extensions.webscripts.Description;

import java.lang.reflect.Method;

/**
 * @author Laurent Van der Linden
 */
public interface ResolutionParameters {
    public Method getUriMethod();

    public Description getDescription();

    public Object getHandler();

    public Class<?> getHandlerClass();
}
