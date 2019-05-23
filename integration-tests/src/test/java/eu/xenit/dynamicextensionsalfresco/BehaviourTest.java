package eu.xenit.dynamicextensionsalfresco;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Integration tests for behaviors, making use of the '/behaviours/...' endpoints which are defined in the
 * 'test-bundle'
 */
public class BehaviourTest extends RestAssuredTest {

    private static final Logger logger = LoggerFactory.getLogger(BehaviourTest.class);

    @Test
    public void testBehaviour_OnCreateNodePolicy() {
        logger.info("Test scenario: adding a property to a node using an annotated 'OnCreateNodePolicy' behaviour");

        given()
                .log().ifValidationFails()
                .when()
                .get("s/dynamic-extensions/testing/behaviours/OnCreateNodePolicy")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .body(equalTo("\"TestBehaviour successfully processed\""));
    }
}
