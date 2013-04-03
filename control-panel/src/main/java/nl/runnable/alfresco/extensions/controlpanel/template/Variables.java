package nl.runnable.alfresco.extensions.controlpanel.template;

public class Variables {

	/**
	 * Refers to {@link TemplateBundle}s that represents framework bundles.
	 */
	public static final String FRAMEWORK_BUNDLES = "frameworkBundles";

	/**
	 * Refers to {@link TemplateBundle}s that represents extension bundles.
	 */
	public static final String EXTENSION_BUNDLES = "extensionBundles";

	/**
	 * Refers to Strings that specify the full paths to directories for installing OSGi bundle JARs.
	 */
	public static final String FILE_INSTALL_PATHS = "fileInstallPaths";

	/**
	 * Refers to a {@link TemplateBundle}.
	 */
	public static final String BUNDLE = "bundle";

	/**
	 * Refers to a resource ID.
	 */
	public static final String ID = "id";

	private Variables() {
	}
}
