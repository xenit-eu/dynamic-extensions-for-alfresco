package com.github.dynamicextensionsalfresco.osgi.io;

import aQute.bnd.osgi.Analyzer;
import com.github.dynamicextensionsalfresco.osgi.ManifestUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import org.apache.commons.io.FilenameUtils;

public class FileUtil {

    public static Attributes jarAttributes(File file) throws IOException {

        try (JarFile jarFile = new JarFile(file)) {
            Manifest manifest = jarFile.getManifest();
            return manifest.getMainAttributes();
        }
    }

    public static File toTempFile(InputStream inputStream, String prefix, String suffix) throws IOException {
        try {
            File tempFile = java.io.File.createTempFile(prefix, suffix);
            tempFile.deleteOnExit();

            Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            return tempFile;

        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    public static File convertToBundle(File file, String fileName) {
        try {
            JarFile jar = new JarFile(file);

            Analyzer analyzer = new Analyzer();
            String manifestVersion = ManifestUtils.getImplementationVersion(jar);

            if (manifestVersion != null) {
                analyzer.setBundleVersion(manifestVersion);
            }

            String name = ManifestUtils.getImplementationTitle(jar);
            if (name == null) {
                name = FilenameUtils.removeExtension(fileName);
            }
            analyzer.setBundleSymbolicName(name);

            analyzer.setJar(file);
            analyzer.setImportPackage("*;resolution:=optional");
            analyzer.setExportPackage("*");

            analyzer.analyze();
            Manifest manifest = analyzer.calcManifest();
            analyzer.getJar().setManifest(manifest);
            File wrappedTempFile = File.createTempFile("bundled", ".jar");
            analyzer.save(wrappedTempFile, true);

            return wrappedTempFile;
        } catch (Exception ex) {
            throw new RuntimeException("Failed converting file to OSGi bundle", ex);
        }


    }
}
