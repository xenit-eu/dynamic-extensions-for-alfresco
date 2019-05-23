package eu.xenit.dynamicextensionsalfresco;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import org.junit.Test;

/**
 * Integration tests for behaviors, making use of the '/behaviours/...' endpoints of the 'test-bundle'
 */
public class BehaviourTest extends RestAssuredTest {

    @Test
    public void testBehaviour_OnCreateNodePolicy() {
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
