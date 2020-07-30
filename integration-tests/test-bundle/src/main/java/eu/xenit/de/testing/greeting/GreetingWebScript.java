package eu.xenit.de.testing.greeting;

import static eu.xenit.de.testing.Constants.TEST_WEBSCRIPTS_BASE_URI;
import static eu.xenit.de.testing.Constants.TEST_WEBSCRIPTS_FAMILY;

import com.github.dynamicextensionsalfresco.webscripts.annotations.Uri;
import com.github.dynamicextensionsalfresco.webscripts.annotations.WebScript;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@WebScript(families = TEST_WEBSCRIPTS_FAMILY, baseUri = TEST_WEBSCRIPTS_BASE_URI + "/greeting")
@SuppressWarnings("unused")
public class GreetingWebScript {

    private final GreetingService greetingService;

    @Autowired
    public GreetingWebScript(
            @Qualifier("greetingServiceWrapper") GreetingServiceWrapper first,
            @Qualifier("anotherGreetingServiceWrapper") GreetingServiceWrapper second) {

        if (!Objects.equals(first.getGreetingService(), second.getGreetingService())) {
            throw new IllegalStateException("Requiring only one GreetingService to rule them all");
        }

        this.greetingService = first.getGreetingService();
    }

    @Uri
    public ResponseEntity<String> getGreeting() {
        return new ResponseEntity<>(greetingService.getGreeting(), HttpStatus.OK);
    }

    @Uri("/number-of-instances")
    public ResponseEntity<Integer> getNumberOfInstances() {
        return new ResponseEntity<>(GreetingService.getNumberOfInstances(), HttpStatus.OK);
    }


}
