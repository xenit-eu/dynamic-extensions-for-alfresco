package com.github.dynamicextensionsalfresco.osgi;

import org.osgi.framework.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * Tools for extracting metadata from Jar files using the standard Implementation headers
 *
 * @author Laurent Van der Linden
 */
public class ManifestUtils {
    private final static Logger logger = LoggerFactory.getLogger(ManifestUtils.class);

    public static String getImplementationTitle(final JarFile jarFile) throws IOException {
        final List<Attributes> attributesList = getAllJarAttributes(jarFile);
        for (final Attributes attributes : attributesList) {
            String title = (String) attributes.get(Attributes.Name.IMPLEMENTATION_TITLE);
            if (title != null) {
                return title;
            }
        }
        return null;
    }

    public static String parseImplementationVersion(final JarFile jarFile) throws IOException {
        final List<Attributes> attributesList = getAllJarAttributes(jarFile);
        for (final Attributes attributes : attributesList) {
            String version = (String) attributes.get(Attributes.Name.IMPLEMENTATION_VERSION);
            if (version != null) {
                try {
                    version = version.split("\\s")[0];
                    Version.parseVersion(version);
                    return version;
                } catch (final IllegalArgumentException e) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Found invalid version '{}' in Implementation-Version header in JAR '{}'.",
                            version, jarFile.getName());
                    }
                }
            }
        }
        return null;
    }

    public static List<Attributes> getAllJarAttributes(JarFile jarFile) throws IOException {
        final Manifest manifest = jarFile.getManifest();
        if (manifest == null) {
            return Collections.emptyList();
        }
        final List<Attributes> attributesList = new ArrayList<Attributes>(manifest.getEntries().size() + 1);
        attributesList.add(manifest.getMainAttributes());
        attributesList.addAll(manifest.getEntries().values());
        return attributesList;
    }
}
