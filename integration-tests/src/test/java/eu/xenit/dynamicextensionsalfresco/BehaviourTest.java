package eu.xenit.dynamicextensionsalfresco;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyString;

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
        logger.info("Test scenario: check that the 'OnCreateNodePolicy' behaviour is triggered when creating an "
                + "applicable node");

        given()
                .log().ifValidationFails()
                .when()
                .post("s/dynamic-extensions/testing/behaviours/OnCreateNodePolicy")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .body(equalTo("\"TestBehaviour successfully processed\""));
    }

    @Test
    public void testBehaviour_OnCreateNodePolicy_notTriggeredIfTypeNotApplicable() {
        logger.info("Test scenario: check that the 'OnCreateNodePolicy' behaviour is not triggered when creating a "
                + "node that is not applicable");

        given()
                .log().ifValidationFails()
                .when()
                .post("s/dynamic-extensions/testing/behaviours/OnCreateNodePolicyNotTriggeredIfTypeNotApplicable")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .body(isEmptyString());
    }
}
