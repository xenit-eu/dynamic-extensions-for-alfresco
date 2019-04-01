package com.github.dynamicextensionsalfresco.webscripts;

import com.github.dynamicextensionsalfresco.webscripts.annotations.HttpMethod;
import com.github.dynamicextensionsalfresco.webscripts.annotations.Transaction;
import com.github.dynamicextensionsalfresco.webscripts.annotations.Uri;
import com.github.dynamicextensionsalfresco.webscripts.annotations.WebScript;
import org.springframework.stereotype.Component;

@Component
@WebScript
@Transaction(
        readOnly = false
)
public final class TypeTransactionWebscript {

    @Uri({"/ttxdefault"})
    public final void defaults() {
    }

    @Uri({"/ttxgetwrite"})
    @Transaction(
            readOnly = false
    )
    public final void getwrite() {
    }

    @Uri(
            method = HttpMethod.POST,
            value = {"/ttxpost"}
    )
    public final void post() {
    }
}
