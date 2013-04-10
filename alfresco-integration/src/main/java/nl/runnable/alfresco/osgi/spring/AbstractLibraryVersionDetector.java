package nl.runnable.alfresco.osgi.spring;

import org.springframework.core.io.Resource;

/**
 * Abstract base class for {@link LibraryVersionDetector} implementations.
 * 
 * @author Laurens Fridael
 * 
 */
public abstract class AbstractLibraryVersionDetector implements LibraryVersionDetector {

	/* State */

	private String detectedVersion;

	/* Main operations */

    @Override
    public final String detectLibraryVersion(final String packageName, final Resource classResource) {
		String version = null;
		if (packageName.startsWith(getBasePackageName())) {
			if (detectedVersion == null) {
				detectedVersion = doDetectLibraryVersion(packageName);
			}
			version = detectedVersion;
		}
		return version;
	}

	/* Utility operations */

	/**
	 * Obtains the base package name.
	 * 
	 * @return
	 */
	protected abstract String getBasePackageName();

	/**
	 * Detects the library version based on the package name.
	 * 
	 * @param packageName
	 *            The exact package name.
	 * @return
	 */
	protected abstract String doDetectLibraryVersion(String packageName);

}
