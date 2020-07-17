package eu.xenit.de.testing.greeting;

import static eu.xenit.de.testing.Constants.TEST_WEBSCRIPTS_BASE_URI;
import static eu.xenit.de.testing.Constants.TEST_WEBSCRIPTS_FAMILY;

import com.github.dynamicextensionsalfresco.webscripts.annotations.Uri;
import com.github.dynamicextensionsalfresco.webscripts.annotations.WebScript;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@WebScript(families = TEST_WEBSCRIPTS_FAMILY, baseUri = TEST_WEBSCRIPTS_BASE_URI + "/greeting")
@SuppressWarnings("unused")
public class GreetingWebScript {

    @Autowired
    private GreetingService greetingService;

    @Uri
    public ResponseEntity<String> getGreeting() {
        return new ResponseEntity<>(greetingService.getGreeting(), HttpStatus.OK);
    }

    @Uri("/number-of-instances")
    public ResponseEntity<Integer> getNumberOfInstances() {
        return new ResponseEntity<>(greetingService.getNumberOfInstances(), HttpStatus.OK);
    }


}
