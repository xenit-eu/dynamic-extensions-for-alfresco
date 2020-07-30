package eu.xenit.dynamicextensionsalfresco;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Integration tests that will cause Spring proxy magic to happen in Alfresco. Makes use of the '/greeting/...'
 * endpoints which are defined in the 'test-bundle'
 * <p>
 * Interesting read on Spring and CGLIB proxies: http://www.javabyexamples.com/cglib-proxying-in-spring-configuration
 */
public class SpringProxyMagicTest extends RestAssuredTest {

    private static final Logger logger = LoggerFactory.getLogger(BehaviourTest.class);

    @Test
    public void triggerAndTestSpringProxyMagic() {
        logger.info("Test scenario: make use of a Bean which was initialized by an @Configuration annotated class."
                + " By doing so, we try to proactively trigger a ClassNotFoundException for any of the Spring CGLIB"
                + "related classes.");

        given()
                .log().ifValidationFails()
                .when()
                .get("s/dynamic-extensions/testing/greeting")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .body(equalTo("\"Hello there, adventurer!\""));

        for (int i = 0; i < 3; i++) {
            // Beans initialized via the @Bean annotation in an @Configuration class should only be initialized once
            given()
                    .log().ifValidationFails()
                    .when()
                    .get("s/dynamic-extensions/testing/greeting/number-of-instances")
                    .then()
                    .log().ifValidationFails()
                    .statusCode(200)
                    .body(equalTo("1"));
        }
    }

}
