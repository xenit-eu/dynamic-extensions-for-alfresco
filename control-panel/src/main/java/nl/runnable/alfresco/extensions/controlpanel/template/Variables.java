package nl.runnable.alfresco.extensions.controlpanel.template;

import java.util.List;

import nl.runnable.alfresco.extensions.controlpanel.Container;
import nl.runnable.alfresco.osgi.SystemPackage;

import org.osgi.framework.Bundle;

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
	 * Refers to a {@link Container}.
	 */
	public static final String CONFIGURATION = "configuration";

	/**
	 * Refers to a {@link TemplateBundle}.
	 */
	public static final String BUNDLE = "bundle";

	/**
	 * Refers to a resource ID.
	 */
	public static final String ID = "id";

	/**
	 * Refers to a {@link TemplateBundle} representing a newly-installed {@link Bundle}.
	 */
	public static final String INSTALLED_BUNDLE = "installedBundle";

	/**
	 * Refers to a {@link TemplateBundle} representing an uninstalled {@link Bundle}.
	 */
	public static final String DELETED_BUNDLE = "deletedBundle";

	/**
	 * Refers to a String specifying an error message.
	 */
	public static final String ERROR_MESSAGE = "errorMessage";

	/**
	 * Refers to a String specifying a success message.
	 */
	public static final String SUCCESS_MESSAGE = "successMessage";

	/**
	 * Refers to a String specifying the repository location where bundles are stored.
	 */
	public static final String REPOSITORY_STORE_LOCATION = "repositoryStoreLocation";

	/**
	 * Refers to a {@link List} of {@link SystemPackage}s.
	 */
	public static final String SYSTEM_PACKAGES = "systemPackages";

	/**
	 * Refers to an int specifying the number of {@link SystemPackage}s.
	 * <p>
	 * Specifying the number of system packages in a separate variable allows us to lazily obtain the actual collection.
	 */
	public static final String SYSTEM_PACKAGE_COUNT = "systemPackageCount";

	/**
	 * Refers to a String with the repository path to the {@link SystemPackage} cache file.
	 */
	public static final String SYSTEM_PACKAGE_CACHE_NODEREF = "systemPackageCacheNodeRef";

	/**
	 * Refers to a Boolean indicating if the {@link SystemPackage} cache exists.
	 */
	public static final String SYSTEM_PACKAGE_CACHE_EXISTS = "systemPackageCacheExists";

	/**
	 * Refers to a Boolean that indicates if the Framework can be restarted.
	 */
	public static final String CAN_RESTART_FRAMEWORK = "canRestartFramework";

	/**
	 * Refers to a {@link List} of {@link SystemPackage}s.
	 */
	public static final String SERVICES_BY_BUNDLE = "servicesByBundle";

	/**
	 * Refers to the name of the fully-authenticated user.
	 */
	public static final String CURRENT_USER = "currentUser";

	/**
	 * Refers to a collection of {@link TemplateWebScript}s.
	 */
	public static final String WEB_SCRIPTS = "webScripts";

	private Variables() {
	}
}
