package com.github.dynamicextensionsalfresco.webscripts

import org.springframework.extensions.webscripts.WebScriptRequest
import org.springframework.extensions.webscripts.WrappingWebScriptRequest
import java.util.LinkedHashMap

public class AnnotationWebScriptRequest(val webScriptRequest: WebScriptRequest) : WebScriptRequest by webScriptRequest, WrappingWebScriptRequest {
    override fun toString(): String {
        return next.toString()
    }

    public val model: MutableMap<String, Any> = LinkedHashMap()

    public var thrownException: Throwable? = null
        set

    override fun getNext(): WebScriptRequest {
        if (webScriptRequest is WrappingWebScriptRequest) {
            return webScriptRequest.next
        } else {
            return webScriptRequest
        }
    }
}
