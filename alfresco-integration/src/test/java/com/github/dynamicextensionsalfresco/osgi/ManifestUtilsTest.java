package com.github.dynamicextensionsalfresco.osgi;

import org.junit.Test;
import org.osgi.framework.Version;

import static org.junit.Assert.*;

/**
 * @author Laurent Van der Linden
 */
public class ManifestUtilsTest {
    @Test
    public void testQualifiedVersionParsing() {
        final Version version = ManifestUtils.parseImplementationVersionValue("5.13-alf-20130918");
        assertEquals(5, version.getMajor());
        assertEquals(13, version.getMinor());
        assertEquals(0, version.getMicro());
        assertEquals("alf-20130918", version.getQualifier());
    }

    @Test
    public void testMicroVersionParsing() {
        final Version version = ManifestUtils.parseImplementationVersionValue("5.13.1");
        assertEquals(5, version.getMajor());
        assertEquals(13, version.getMinor());
        assertEquals(1, version.getMicro());
    }

    @Test
    public void testQualifiedMicroVersionParsing() {
        final Version version = ManifestUtils.parseImplementationVersionValue("5.13.1-alf-x");
        assertEquals(5, version.getMajor());
        assertEquals(13, version.getMinor());
        assertEquals(1, version.getMicro());
        assertEquals("alf-x", version.getQualifier());
    }

    @Test
    public void testMinorVersionParsing() {
        final Version version = ManifestUtils.parseImplementationVersionValue("5.13");
        assertEquals(5, version.getMajor());
        assertEquals(13, version.getMinor());
        assertEquals(0, version.getMicro());
    }
}
