package com.github.dynamicextensionsalfresco.webscripts.resolutions;

import org.springframework.extensions.webscripts.Description;

import java.lang.reflect.Method;

/**
 * @author Laurent Van der Linden
 */
public interface ResolutionParameters {
    Method getUriMethod();

    Description getDescription();

    Object getHandler();

    Class<?> getHandlerClass();
}
