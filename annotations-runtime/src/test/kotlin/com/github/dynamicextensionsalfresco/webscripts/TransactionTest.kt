package com.github.dynamicextensionsalfresco.webscripts

import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.extensions.webscripts.Description
import org.springframework.extensions.webscripts.Match
import org.springframework.extensions.webscripts.UriIndex

import org.junit.Assert.assertEquals
import kotlin.properties.Delegates

/**
 * Verify @Authentication can be overridden on a method basis.

 * @author Laurent Van der Linden
 */
public class TransactionTest : AbstractWebScriptAnnotationsTest() {
	Autowired
	private var uriIndex: UriIndex? = null

	Test
    public fun testTransactionOverridesUntyped() {
		assertEquals(
				Description.TransactionCapability.readonly,
				uriIndex!!.findWebScript("GET", "/txdefault")
						.getWebScript()
						.getDescription()
						.getRequiredTransactionParameters()
						.getCapability()
		)

		assertEquals(
				Description.TransactionCapability.readwrite,
				uriIndex!!.findWebScript("GET", "/txgetwrite")
						.getWebScript()
						.getDescription()
						.getRequiredTransactionParameters()
						.getCapability()
		)

		assertEquals(
				Description.TransactionCapability.readwrite,
				uriIndex!!.findWebScript("POST", "/txpost")
						.getWebScript()
						.getDescription()
						.getRequiredTransactionParameters()
						.getCapability()
		)

    }

	Test
    public fun testTransactionOverridesWithTypeAnnotation() {
		assertEquals(
				Description.TransactionCapability.readwrite,
				uriIndex!!.findWebScript("GET", "/ttxdefault")
						.getWebScript()
						.getDescription()
						.getRequiredTransactionParameters()
						.getCapability()
		)

		assertEquals(
				Description.TransactionCapability.readwrite,
				uriIndex!!.findWebScript("GET", "/ttxgetwrite")
						.getWebScript()
						.getDescription()
						.getRequiredTransactionParameters()
						.getCapability()
		)

		assertEquals(
				Description.TransactionCapability.readwrite,
				uriIndex!!.findWebScript("POST", "/ttxpost")
						.getWebScript()
						.getDescription()
						.getRequiredTransactionParameters()
						.getCapability()
		)

    }

}
