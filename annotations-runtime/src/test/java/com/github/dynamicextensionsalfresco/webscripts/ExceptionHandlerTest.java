package com.github.dynamicextensionsalfresco.webscripts;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assume.assumeTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

import java.util.Objects;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.SpringVersion;
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
        assumeTrue("Annotated default methods in interfaces is only supported starting from Alfresco 6",
                getSpringMajorVersion() >= 5);

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

    private static int getSpringMajorVersion() {
        return Integer.parseInt(Objects.requireNonNull(SpringVersion.getVersion()).substring(0, 1));
    }
}
