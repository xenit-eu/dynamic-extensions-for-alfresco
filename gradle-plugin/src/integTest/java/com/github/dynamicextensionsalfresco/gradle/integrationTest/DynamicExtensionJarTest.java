package com.github.dynamicextensionsalfresco.gradle.integrationTest;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import aQute.bnd.osgi.Constants;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import org.junit.Test;

public class DynamicExtensionJarTest extends AbstractIntegrationTest {
    private Path integrationTests = Paths.get("src/integTest/resources/com/github/dynamicextensionsalfresco/gradle/integrationTest");


    @Test
    public void simpleProject() throws IOException {
        buildProject(integrationTests.resolve("simple-project"), "jar");

        Path buildFolder = testProjectDir.getRoot().toPath().resolve("build");

        Path jarFile = buildFolder.resolve("libs/simple-project.jar");

        assertPath(Files::exists, jarFile);

        FileSystem jarFs = FileSystems.newFileSystem(jarFile, null);

        assertPath(Files::exists, jarFs.getPath("META-INF/MANIFEST.MF"));
        assertPath(Files::exists, jarFs.getPath("com/github/dynamicextensionsalfresco/examples/TemplateWebScript.class"));

        try(InputStream manifestInput = Files.newInputStream(jarFs.getPath("META-INF/MANIFEST.MF"))) {
            Manifest jarManifest = new Manifest(manifestInput);
            Attributes mainAttributes = jarManifest.getMainAttributes();

            assertEquals("true", mainAttributes.getValue("Alfresco-Dynamic-Extension"));
            assertEquals("*", mainAttributes.getValue(Constants.DYNAMICIMPORT_PACKAGE));
            String[] packageImports = mainAttributes.getValue(Constants.IMPORT_PACKAGE).split(",");
            assertArrayEquals(new String[] {
                    "com.github.dynamicextensionsalfresco.webscripts.annotations",
                    "org.springframework.extensions.webscripts",
                    "org.springframework.stereotype",
            }, packageImports);

            assertNull(mainAttributes.getValue(Constants.EXPORT_PACKAGE));
        }
    }

    @Test
    public void bndConfig() throws IOException {
        buildProject(integrationTests.resolve("bnd-config"), "jar");

        Path buildFolder = testProjectDir.getRoot().toPath().resolve("build");

        Path jarFile = buildFolder.resolve("libs/bnd-config.jar");

        assertPath(Files::exists, jarFile);

        FileSystem jarFs = FileSystems.newFileSystem(jarFile, null);

        assertPath(Files::exists, jarFs.getPath("META-INF/MANIFEST.MF"));
        assertPath(Files::exists, jarFs.getPath("com/github/dynamicextensionsalfresco/examples/TemplateWebScript.class"));

        try(InputStream manifestInput = Files.newInputStream(jarFs.getPath("META-INF/MANIFEST.MF"))) {
            Manifest jarManifest = new Manifest(manifestInput);
            Attributes mainAttributes = jarManifest.getMainAttributes();

            assertEquals("true", mainAttributes.getValue("Alfresco-Dynamic-Extension"));
            assertEquals("*", mainAttributes.getValue(Constants.DYNAMICIMPORT_PACKAGE));
            String[] packageImports = mainAttributes.getValue(Constants.IMPORT_PACKAGE).split(",");
            assertArrayEquals(new String[] {
                    "com.github.dynamicextensionsalfresco.webscripts.annotations",
                    "org.springframework.extensions.webscripts",
                    "org.springframework.stereotype",
            }, packageImports);

            assertEquals("com.github.dynamicextensionsalfresco.examples", mainAttributes.getValue(Constants.EXPORT_PACKAGE).split(";")[0]);
        }
    }

    @Test
    public void bndConfigOverwrites() throws IOException {
        buildProject(integrationTests.resolve("bnd-config-overwrites"), "jar");

        Path buildFolder = testProjectDir.getRoot().toPath().resolve("build");

        Path jarFile = buildFolder.resolve("libs/bnd-config-overwrites.jar");

        assertPath(Files::exists, jarFile);

        FileSystem jarFs = FileSystems.newFileSystem(jarFile, null);

        assertPath(Files::exists, jarFs.getPath("META-INF/MANIFEST.MF"));
        assertPath(Files::exists, jarFs.getPath("com/github/dynamicextensionsalfresco/examples/TemplateWebScript.class"));

        try(InputStream manifestInput = Files.newInputStream(jarFs.getPath("META-INF/MANIFEST.MF"))) {
            Manifest jarManifest = new Manifest(manifestInput);
            Attributes mainAttributes = jarManifest.getMainAttributes();

            assertEquals("true", mainAttributes.getValue("Alfresco-Dynamic-Extension"));
            assertNull(mainAttributes.getValue(Constants.DYNAMICIMPORT_PACKAGE));
            String[] packageImports = mainAttributes.getValue(Constants.IMPORT_PACKAGE).split(",");
            assertArrayEquals(new String[] {
                    "com.github.dynamicextensionsalfresco.webscripts.annotations",
            }, packageImports);

        }
    }
}
