package eu.xenit.de.testing.exceptionhandlers;

import static eu.xenit.de.testing.Constants.TEST_WEBSCRIPTS_BASE_URI;
import static eu.xenit.de.testing.Constants.TEST_WEBSCRIPTS_FAMILY;

import com.github.dynamicextensionsalfresco.webscripts.annotations.Uri;
import com.github.dynamicextensionsalfresco.webscripts.annotations.WebScript;
import com.github.dynamicextensionsalfresco.webscripts.resolutions.JsonWriterResolution;
import com.github.dynamicextensionsalfresco.webscripts.resolutions.Resolution;
import org.json.JSONException;
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

    @Uri(value = "/JsonResponseWriterTest")
    public Resolution jsonResponseWriterTest() {
        return new JsonWriterResolution() {
            @Override
            protected void writeJson(org.json.JSONWriter jsonWriter) throws JSONException {
                jsonWriter.object().key("message").value("I was made with a JsonWriterResolution").endObject();
            }
        };
    }
}
