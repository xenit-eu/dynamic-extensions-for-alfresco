package com.github.dynamicextensionsalfresco.webscripts

import com.github.dynamicextensionsalfresco.webscripts.annotations.HttpMethod
import com.github.dynamicextensionsalfresco.webscripts.annotations.Transaction
import com.github.dynamicextensionsalfresco.webscripts.annotations.Uri
import com.github.dynamicextensionsalfresco.webscripts.annotations.WebScript
import org.springframework.stereotype.Component

Component
WebScript
Transaction(readOnly = false)
class TypeTransactionWebscript {
    Uri("/ttxdefault")
    public fun defaults() {}

    Uri("/ttxgetwrite")
    @Transaction(readOnly = false)
    public fun getwrite() {}

    Uri("/ttxpost", method = HttpMethod.POST)
    public fun post() {}
}
