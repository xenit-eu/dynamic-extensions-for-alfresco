package com.github.dynamicextensionsalfresco.webscripts;

import com.github.dynamicextensionsalfresco.webscripts.annotations.Authentication;
import com.github.dynamicextensionsalfresco.webscripts.annotations.AuthenticationType;
import com.github.dynamicextensionsalfresco.webscripts.annotations.Uri;
import org.springframework.stereotype.Component;

@Component
@com.github.dynamicextensionsalfresco.webscripts.annotations.WebScript
@Authentication(AuthenticationType.ADMIN)
class AuthenticationWebscript {
    @Uri("/open")
    @Authentication(AuthenticationType.NONE)
    public void open() {}

    @Uri("/standard")
    public void standard() {}

}
