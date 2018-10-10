package com.github.dynamicextensionsalfresco.osgi

import aQute.bnd.osgi.Analyzer
import java.io.File
import java.io.InputStream
import java.util.jar.Attributes
import java.util.jar.JarFile

fun File.jarAttributes(): Attributes {
    val jarFile = JarFile(this)
    try {
        val manifest = jarFile.manifest
        return manifest.mainAttributes
    } finally {
        jarFile.close()
    }
}

fun InputStream.toTempFile(prefix: String, suffix: String): File {
    this.use { iStream ->
        val tempFile = File.createTempFile(prefix, suffix)
        tempFile.deleteOnExit()
        tempFile.outputStream().use { iStream.copyTo(it) }

        return tempFile
    }
}

fun File.convertToBundle(fileName: String): File {
    val jar = JarFile(this)

    val analyzer = Analyzer()
    val manifestVersion = ManifestUtils.getImplementationVersion(jar)
    if (manifestVersion != null) {
        analyzer.bundleVersion = manifestVersion
    }
    var name = ManifestUtils.getImplementationTitle(jar)
    if (name == null) {
        name = fileName.replaceFirst("^(.+)\\.\\w+$".toRegex(), "$1")
    }
    analyzer.setBundleSymbolicName(name)

    analyzer.setJar(this)
    analyzer.setImportPackage("*;resolution:=optional")
    analyzer.setExportPackage("*")
    analyzer.analyze()
    val manifest = analyzer.calcManifest()
    analyzer.jar.manifest = manifest
    val wrappedTempFile = File.createTempFile("bundled", ".jar")
    analyzer.save(wrappedTempFile, true)

    return wrappedTempFile
}