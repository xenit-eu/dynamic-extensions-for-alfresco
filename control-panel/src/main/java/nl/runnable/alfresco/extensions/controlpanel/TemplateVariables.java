package nl.runnable.alfresco.extensions.controlpanel;

class TemplateVariables {

	/**
	 * Refers to {@link TemplateBundle}s that represents framework bundles.
	 */
	static final String FRAMEWORK_BUNDLES = "frameworkBundles";

	/**
	 * Refers to {@link TemplateBundle}s that represents extension bundles.
	 */
	static final String EXTENSION_BUNDLES = "extensionBundles";

	/**
	 * Refers to Strings that specify the full paths to directories for installing OSGi bundle JARs.
	 */
	static final String FILE_INSTALL_PATHS = "fileInstallPaths";

	/**
	 * Refers to a {@link TemplateBundle}.
	 */
	static final String BUNDLE = "bundle";

	/**
	 * Refers to a resource ID.
	 */
	static final String ID = "id";

	private TemplateVariables() {
	}
}
