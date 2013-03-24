package nl.runnable.alfresco.dynamicextensions;

import nl.runnable.alfresco.metadata.ExtensionMetadata;

class TemplateVariables {

	/**
	 * Refers to {@link ExtensionMetadata} instances representing core bundles.
	 */
	static final String CORE_BUNDLES = "coreBundles";

	/**
	 * Refers to {@link ExtensionMetadata} instances representing extensions.
	 */
	static final String EXTENSIONS = "extensions";

	/**
	 * Refers to Strings that specify the full paths to directories for installing OSGi bundle JARs.
	 */
	static final String FILE_INSTALL_PATHS = "fileInstallPaths";

	private TemplateVariables() {
	}
}
