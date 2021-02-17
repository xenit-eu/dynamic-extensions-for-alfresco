package com.github.dynamicextensionsalfresco.webscripts;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class ExceptionHandlerTest extends AbstractWebScriptAnnotationsTest {

    @Autowired
    private ExceptionHandlerExample handler;

    @Test
    public void testHandleExceptionOfOneType() {
        handleGet("/throwIllegalArgumentException");
        assertNotNull(handler.illegalArgumentException);
        assertNotNull(handler.throwable);
        assertNull(handler.illegalStateException);
    }

    @Test
    public void testHandleExceptionOfAnotherType() {
        handleGet("/throwIllegalStateException");
        assertNull(handler.illegalArgumentException);
        assertNotNull(handler.throwable);
        assertNotNull(handler.illegalStateException);
    }

    @Test
    public void testHandleExceptionByDefaultInterfaceMethod() {
        handleGet("/throwUnsupportedOperationException");
        assertNull(handler.illegalArgumentException);
        assertNotNull(handler.throwable);
        assertNull(handler.illegalStateException);
        verify(handler).handleUnsupportedOperationException(any(UnsupportedOperationException.class));
    }

    @Test
    public void testHandleExceptionByMethodInParent() {
        handleGet("/throwIndexOutOfBoundsException");
        assertNotNull(handler.throwable);
        assertNotNull(handler.indexOutOfBoundsException);
        verify(handler).handleIndexOutOfBoundsException(any(IndexOutOfBoundsException.class));
    }
}
