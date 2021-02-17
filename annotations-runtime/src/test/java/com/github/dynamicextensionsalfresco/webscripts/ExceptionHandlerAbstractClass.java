package com.github.dynamicextensionsalfresco.webscripts;

import static org.junit.Assert.assertNotNull;

import com.github.dynamicextensionsalfresco.webscripts.annotations.ExceptionHandler;

public abstract class ExceptionHandlerAbstractClass extends AbstractWebScriptAnnotationsTest {

    IndexOutOfBoundsException indexOutOfBoundsException;

    @ExceptionHandler(IndexOutOfBoundsException.class)
    protected void handleIndexOutOfBoundsException(final IndexOutOfBoundsException e) {
        indexOutOfBoundsException = e;
    }

}
