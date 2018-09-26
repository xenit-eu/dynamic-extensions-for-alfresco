package com.github.dynamicextensionsalfresco.osgi

import java.io.File

/**
 * Value object representing the OSGi container configuration;

 * @author Laurens Fridael
 */
public class Configuration {

    var frameworkRestartEnabled = true

    var hotDeployEnabled = true

    var repositoryBundlesEnabled = true

    var storageDirectory: File? = null
		get() = field ?: File(System.getProperty("java.io.tmpdir"), "bundles")

    var systemPackageCacheMode: PackageCacheMode? = null

    val systemPackageCache = File(System.getProperty("java.io.tmpdir"), "system-packages.txt")
}
