package eu.xenit.dynamicextensionsalfresco;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonWriterResolutionTest extends RestAssuredTest {

    private static final Logger logger = LoggerFactory.getLogger(JsonWriterResolutionTest.class);

    @Test
    public void testExceptionHandler_jsonResolutionWriter() {
        logger.info("Test scenario: Check that the JsonWriterResponse is instantiable across versions.");

        given().log().ifValidationFails()
                .when().get("s/dynamic-extensions/testing/exceptions/JsonResponseWriterTest")
                .then().log().ifValidationFails()
                .statusCode(200)
                .and()
                .body("message", equalTo("I was made with a JsonWriterResolution"));
    }

}
