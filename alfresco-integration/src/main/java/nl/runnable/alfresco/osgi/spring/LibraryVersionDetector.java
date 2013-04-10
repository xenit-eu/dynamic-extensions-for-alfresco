package nl.runnable.alfresco.osgi.spring;

import org.springframework.core.io.Resource;

/**
 * Strategy for detecting the version of a Java library.
 * 
 * @author Laurens Fridael
 * 
 */
public interface LibraryVersionDetector {

	/**
	 * Attempts to detect the version of a library based on the Java package name.
	 * 
	 * @param packageName
	 *            The Java package name.
	 * @return The library version or null if none could be determined.
	 */
	String detectLibraryVersion(String packageName, Resource classResource);
}
