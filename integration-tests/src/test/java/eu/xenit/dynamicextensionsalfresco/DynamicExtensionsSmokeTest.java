package eu.xenit.dynamicextensionsalfresco;

import static io.restassured.RestAssured.given;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class DynamicExtensionsSmokeTest extends RestAssuredTest {

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
    public void smokeTest_dynamicExtensionsDashboard() {
        final List<String> dashboardPaths = new ArrayList<String>() {{
            add("s/dynamic-extensions/");
            add("s/dynamic-extensions/bundles");
            add("s/dynamic-extensions/web-scripts");
            add("s/dynamic-extensions/container");
            add("s/dynamic-extensions/container/system-packages");
            add("s/dynamic-extensions/container/services");

        }};

        for (final String dashboardPath : dashboardPaths) {
            given()
                    .log().ifValidationFails()
                    .when()
                    .get(dashboardPath)
                    .then()
                    .log().ifValidationFails()
                    .statusCode(200);
        }
    }

}