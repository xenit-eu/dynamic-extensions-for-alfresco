package com.github.dynamicextensionsalfresco.webscripts;

import com.github.dynamicextensionsalfresco.webscripts.annotations.HttpMethod;
import com.github.dynamicextensionsalfresco.webscripts.annotations.Transaction;
import com.github.dynamicextensionsalfresco.webscripts.annotations.Uri;
import com.github.dynamicextensionsalfresco.webscripts.annotations.WebScript;
import org.springframework.stereotype.Component;

@Component
@WebScript
public final class DefaultTransactionWebscript {

    @Uri({"/txdefault"})
    public final void defaults() {
    }

    @Uri({"/txgetwrite"})
    @Transaction(
            readOnly = false
    )
    public final void getwrite() {
    }

    @Uri(
            method = HttpMethod.POST,
            value = {"/txpost"}
    )
    public final void post() {
    }
}
