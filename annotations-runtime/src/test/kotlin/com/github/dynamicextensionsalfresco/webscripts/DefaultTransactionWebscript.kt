package com.github.dynamicextensionsalfresco.webscripts

import com.github.dynamicextensionsalfresco.webscripts.annotations.*
import org.springframework.stereotype.Component

Component
WebScript
class DefaultTransactionWebscript {
    Uri("/txdefault")
    public fun defaults() {}

    Uri("/txgetwrite")
    @Transaction(readOnly = false)
    public fun getwrite() {}

    Uri("/txpost", method = HttpMethod.POST)
    public fun post() {}
}
