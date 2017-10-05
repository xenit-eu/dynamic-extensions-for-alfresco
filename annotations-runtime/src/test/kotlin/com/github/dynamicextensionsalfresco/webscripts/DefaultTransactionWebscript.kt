package com.github.dynamicextensionsalfresco.webscripts

import com.github.dynamicextensionsalfresco.webscripts.annotations.HttpMethod
import com.github.dynamicextensionsalfresco.webscripts.annotations.Transaction
import com.github.dynamicextensionsalfresco.webscripts.annotations.Uri
import com.github.dynamicextensionsalfresco.webscripts.annotations.WebScript
import org.springframework.stereotype.Component

@Component
@WebScript
class DefaultTransactionWebscript {
    @Uri("/txdefault")
    public fun defaults() {}

    @Uri("/txgetwrite")
    @Transaction(readOnly = false)
    public fun getwrite() {}

    @Uri("/txpost", method = HttpMethod.POST)
    public fun post() {}
}
