package com.github.dynamicextensionsalfresco.webscripts;

import com.github.dynamicextensionsalfresco.webscripts.annotations.ExceptionHandler;

public abstract class ExceptionHandlerAbstractClass {

    IndexOutOfBoundsException indexOutOfBoundsException;

    @ExceptionHandler(IndexOutOfBoundsException.class)
    protected void handleIndexOutOfBoundsException(final IndexOutOfBoundsException e) {
        indexOutOfBoundsException = e;
    }

}
