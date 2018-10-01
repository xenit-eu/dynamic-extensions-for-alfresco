package eu.xenit.dynamicextensionsalfresco;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.preemptive;

import io.restassured.RestAssured;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DynamicExtensionsSmokeTest {

    private static final Logger logger = LoggerFactory.getLogger(DynamicExtensionsSmokeTest.class);

    private static final String ALFRESCO_USERNAME = "admin";
    private static final String ALFRESCO_PASSWORD = "admin";

    @BeforeClass
    public static void initializeRestAssured() {
        logger.info("Initializing REST-Assured for smoke tests");

        final String baseURI = "http://" + System.getProperty("alfresco.host", "localhost");
        RestAssured.baseURI = baseURI;
        int port = Integer.parseInt(System.getProperty("alfresco.tcp.8080", "8080"));
        RestAssured.port = port;
        final String basePath = "/alfresco";
        RestAssured.basePath = basePath;

        logger.info("REST-Assured initialized with following URI: {}:{}{}", baseURI, port, basePath);

        RestAssured.authentication = preemptive().basic(ALFRESCO_USERNAME, ALFRESCO_PASSWORD);
    }

    @Test
    public void smokeTest_alfresco() {
        given()
                .log().ifValidationFails()
                .when()
                .get()
                .then()
                .log().ifValidationFails()
                .statusCode(200);
    }

    @Test
    public void smokeTest_dynamicExtensions() {
        given()
                .log().ifValidationFails()
                .when()
                .get("s/dynamic-extensions/")
                .then()
                .log().ifValidationFails()
                .statusCode(200);
    }

}