package com.github.dynamicextensionsalfresco.webscripts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.extensions.webscripts.Description;
import org.springframework.extensions.webscripts.UriIndex;

/**
 * Verify @Authentication can be overridden on a method basis.
 *
 * @author Laurent Van der Linden
 */
public final class TransactionTest extends AbstractWebScriptAnnotationsTest {

    @Autowired
    private UriIndex uriIndex;

    @Test
    public final void testTransactionOverridesUntyped() {
        assertNotNull("uriIndex not correctly autowired", uriIndex);

        assertEquals(
                Description.TransactionCapability.readonly,
                uriIndex.findWebScript("GET", "/txdefault")
                        .getWebScript()
                        .getDescription()
                        .getRequiredTransactionParameters()
                        .getCapability()
        );

        assertEquals(
                Description.TransactionCapability.readwrite,
                uriIndex.findWebScript("GET", "/txgetwrite")
                        .getWebScript()
                        .getDescription()
                        .getRequiredTransactionParameters()
                        .getCapability()
        );

        assertEquals(
                Description.TransactionCapability.readwrite,
                uriIndex.findWebScript("POST", "/txpost")
                        .getWebScript()
                        .getDescription()
                        .getRequiredTransactionParameters()
                        .getCapability()
        );

    }

    @Test
    public final void testTransactionOverridesWithTypeAnnotation() {
        assertNotNull("uriIndex not correctly autowired", uriIndex);

        assertEquals(
                Description.TransactionCapability.readwrite,
                uriIndex.findWebScript("GET", "/ttxdefault")
                        .getWebScript()
                        .getDescription()
                        .getRequiredTransactionParameters()
                        .getCapability()
        );

        assertEquals(
                Description.TransactionCapability.readwrite,
                uriIndex.findWebScript("GET", "/ttxgetwrite")
                        .getWebScript()
                        .getDescription()
                        .getRequiredTransactionParameters()
                        .getCapability()
        );

        assertEquals(
                Description.TransactionCapability.readwrite,
                uriIndex.findWebScript("POST", "/ttxpost")
                        .getWebScript()
                        .getDescription()
                        .getRequiredTransactionParameters()
                        .getCapability()
        );

    }
}
