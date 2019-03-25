package eu.xenit.dynamicextensionsalfresco;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.internal.util.IOUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class HotDeploySmokeTest extends RestAssuredTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void hotDeployOsgiJar_usingApi() throws IOException {
        final int bundleId = given()
                .config(RestAssured.config()
                        .encoderConfig(EncoderConfig.encoderConfig()
                                .appendDefaultContentCharsetToContentTypeIfUndefined(false)))
                .body(getOsgiTestJar("osgi-jar-MANIFEST.MF"))
                .contentType("application/java-archive")
                .log().ifValidationFails()
                .when()
                .post("s/dynamic-extensions/api/bundles")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .body("message", is("Installed bundle alfresco.osgi.test.jar 1.0.3"))
                .extract()
                .body()
                .path("bundleId");

        // Hot-deploy new version of bundle
        given()
                .config(RestAssured.config()
                        .encoderConfig(EncoderConfig.encoderConfig()
                                .appendDefaultContentCharsetToContentTypeIfUndefined(false)))
                .body(getOsgiTestJar("osgi-jar-updated-MANIFEST.MF"))
                .contentType("application/java-archive")
                .log().ifValidationFails()
                .when()
                .post("s/dynamic-extensions/api/bundles")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .body("bundleId", is(bundleId))
                .body("message", is("Installed bundle alfresco.osgi.test.jar 1.0.4.SNAPSHOT"));
    }

    @Test
    public void hotDeployOsgiJar_usingDashboard() throws IOException {
        given()
                .multiPart("file", getOsgiTestJar("osgi-jar-MANIFEST.MF"))
                .log().ifValidationFails()
                .when()
                .post("s/dynamic-extensions/bundles/install")
                .then()
                .log().ifValidationFails()
                .statusCode(302); // redirects to the '/bundles' dashboard

        // Hot-deploy new version of bundle
        given()
                .multiPart("file", getOsgiTestJar("osgi-jar-updated-MANIFEST.MF"))
                .log().ifValidationFails()
                .when()
                .post("s/dynamic-extensions/bundles/install")
                .then()
                .log().ifValidationFails()
                .statusCode(302); // redirects to the '/bundles' dashboard
    }

    private File getOsgiTestJar(final String manifestFile) throws IOException {

        final File osgiJar = temporaryFolder.newFile();

        FileOutputStream fout = new FileOutputStream(osgiJar);
        JarOutputStream jos = new JarOutputStream(fout);
        jos.putNextEntry(new ZipEntry("META-INF/"));
        jos.putNextEntry(new ZipEntry("META-INF/MANIFEST.MF"));
        jos.write(IOUtils.toByteArray(
                Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream(manifestFile))));
        jos.closeEntry();
        jos.close();
        fout.close();

        assertThat(Files.exists(osgiJar.toPath()), is(equalTo(true)));

        return osgiJar;
    }

}
