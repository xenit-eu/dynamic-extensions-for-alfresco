package eu.xenit.dynamicextensionsalfresco;

import static io.restassured.RestAssured.preemptive;

import io.restassured.RestAssured;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class RestAssuredTest {

    private static final Logger logger = LoggerFactory.getLogger(RestAssuredTest.class);

    private static final String ALFRESCO_USERNAME = "admin";
    private static final String ALFRESCO_PASSWORD = "admin";

    @BeforeClass
    public static void initializeRestAssured() {
        logger.info("Initializing REST-Assured for smoke tests");

        final String baseURI = "http://" + System.getProperty("haproxy.host", "localhost");
        RestAssured.baseURI = baseURI;
        int port = Integer.parseInt(System.getProperty("haproxy.tcp.80", "8080"));
        RestAssured.port = port;
        final String basePath = "/alfresco";
        RestAssured.basePath = basePath;

        logger.info("REST-Assured initialized with following URI: {}:{}{}", baseURI, port, basePath);

        RestAssured.authentication = preemptive().basic(ALFRESCO_USERNAME, ALFRESCO_PASSWORD);
    }

}
