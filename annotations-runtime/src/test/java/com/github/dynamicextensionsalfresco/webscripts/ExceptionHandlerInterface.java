package com.github.dynamicextensionsalfresco.webscripts;

import static org.junit.Assert.assertNotNull;

import com.github.dynamicextensionsalfresco.webscripts.annotations.ExceptionHandler;

public interface ExceptionHandlerInterface {

    @ExceptionHandler(UnsupportedOperationException.class)
    default void handleUnsupportedOperationException(final UnsupportedOperationException ignore) {
        assertNotNull(ignore);
    }

}
