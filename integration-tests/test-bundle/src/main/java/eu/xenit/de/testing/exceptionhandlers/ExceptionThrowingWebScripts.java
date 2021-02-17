package eu.xenit.de.testing.exceptionhandlers;

import static eu.xenit.de.testing.Constants.TEST_WEBSCRIPTS_BASE_URI;
import static eu.xenit.de.testing.Constants.TEST_WEBSCRIPTS_FAMILY;

import com.github.dynamicextensionsalfresco.webscripts.annotations.Uri;
import com.github.dynamicextensionsalfresco.webscripts.annotations.WebScript;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;

@Component
@WebScript(families = TEST_WEBSCRIPTS_FAMILY, baseUri = TEST_WEBSCRIPTS_BASE_URI + "/exceptions")
public class ExceptionThrowingWebScripts implements ExceptionHandlers {

    @ResponseBody
    @Uri(value = "/IAmATeapot")
    public void throwIAmATeapotException() {
        throw new IAmATeapotException();
    }
}
