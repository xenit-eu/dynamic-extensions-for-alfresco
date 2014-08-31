package com.github.dynamicextensionsalfresco.gradle.configuration

/**
 * @author Laurent Van der Linden
 */
class Authentication {

    String username = "admin"
    String password = "admin"

    boolean asBoolean() {
        (username && password)
    }

    String getBasic() {
        // Use toString() as a workaround for http://jira.codehaus.org/browse/GROOVY-5761
        "$username:$password".toString().bytes.encodeBase64()
    }
}
