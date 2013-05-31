package nl.runnable.alfresco.controlpanel;

import java.io.IOException;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.launch.Framework;
import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.webscripts.servlet.FormData.FormField;

public interface BundleService {

	/**
	 * Obtains the {@link Bundle}s that comprise the core framework.
	 * 
	 * @return
	 */
	List<Bundle> getFrameworkBundles();

	/**
	 * Obtains the {@link Bundle}s that comprise the core framework.
	 * 
	 * @return
	 */
	List<Bundle> getExtensionBundles();

	/**
	 * Obtains the {@link Bundle} for the given id.
	 * 
	 * @param id
	 * @return The matching {@link Bundle} or null if no match could be found.
	 */
	Bundle getBundle(final long id);

	/**
	 * Obtains the {@link Framework} bundle.
	 * 
	 * @return
	 */
	Framework getFramework();

	/**
	 * Installs an uploaded file as a bundle in the repository.
	 * <p>
	 * This implementation first saves the upload to a temporary file. It then attempts to install the file as a bundle.
	 * If this succeeds, it saves the bundle in the repository.
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws BundleException
	 */
	Bundle installBundleInRepository(final FormField file) throws IOException, BundleException;

	/**
	 * Installs a bundle using the given {@link Content} and filename.
	 * 
	 * @param content
	 * @param filename
	 * @return
	 * @throws IOException
	 * @throws BundleException
	 */
	Bundle installBundleInRepository(final Content content) throws IOException, BundleException;

	void uninstallAndDeleteBundle(final Bundle bundle) throws BundleException;

	@SuppressWarnings("rawtypes")
	List<ServiceReference> getAllServices();

	String getBundleRepositoryLocation();

	<T> T getService(final Class<T> service);

}