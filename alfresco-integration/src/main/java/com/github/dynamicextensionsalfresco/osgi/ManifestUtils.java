package com.github.dynamicextensionsalfresco.osgi;

import org.osgi.framework.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static String getImplementationVersion(final JarFile jarFile) throws IOException {
        final List<Attributes> attributesList = getAllJarAttributes(jarFile);
        for (final Attributes attributes : attributesList) {
            String version = (String) attributes.get(Attributes.Name.IMPLEMENTATION_VERSION);
            if (version != null) {
                try {
                    version = parseImplementationVersionValue(version).toString();

                    // validate with Osgi parser
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

    /**
     * Parse an implementation version in a lenient fashion.<br>
     * Supports versions like "5.13-alf-20130918". (no micro segment)
     */
    public static Version parseImplementationVersionValue(String rawVersion) {
        final StringTokenizer st = new StringTokenizer(rawVersion, ".", false);
        final int[] versions = new int[3];
        String qualifier = null;

        final Pattern qualifierPattern = Pattern.compile("^(\\d+)(_|-)(.+)$");
        for (int x = 0; x < versions.length; x++) {
            if (st.hasMoreTokens()) {
                final String token = st.nextToken();
                final Matcher matcher = qualifierPattern.matcher(token);
                if (matcher.matches()) {
                    versions[x] = Integer.parseInt(matcher.group(1));
                    qualifier = matcher.group(3);
                    break;
                }
                try {
                    versions[x] = Integer.parseInt(token);
                } catch (NumberFormatException e) {
                    qualifier = token;
                    break;
                }
            }
        }

        return new Version(versions[0], versions[1], versions[2], qualifier);
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
