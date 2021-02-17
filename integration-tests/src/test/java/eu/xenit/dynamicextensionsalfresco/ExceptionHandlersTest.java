package eu.xenit.dynamicextensionsalfresco;

import static io.restassured.RestAssured.given;
import static org.junit.Assume.assumeTrue;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionHandlersTest extends RestAssuredTest {

    private static final Logger logger = LoggerFactory.getLogger(BehaviourTest.class);

    @Test
    public void testExceptionHandler_iAmATeapot() {
        assumeTrue("Annotated default methods in interfaces is only supported starting from Alfresco 6",
                getAlfrescoMajorVersion() >= 6);

        logger.info("Test scenario: check that the 'OnCreateNodePolicy' behaviour is triggered when creating an "
                + "applicable node");

        given()
                .log().ifValidationFails()
                .when()
                .get("s/dynamic-extensions/testing/exceptions/IAmATeapot")
                .then()
                .log().ifValidationFails()
                .statusCode(418);
    }

}
