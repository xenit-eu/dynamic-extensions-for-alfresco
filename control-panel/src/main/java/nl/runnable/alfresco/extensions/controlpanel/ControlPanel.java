package nl.runnable.alfresco.extensions.controlpanel;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import nl.runnable.alfresco.extensions.controlpanel.template.TemplateBundle;
import nl.runnable.alfresco.extensions.controlpanel.template.Variables;
import nl.runnable.alfresco.osgi.Configuration;
import nl.runnable.alfresco.webscripts.annotations.Attribute;
import nl.runnable.alfresco.webscripts.annotations.Authentication;
import nl.runnable.alfresco.webscripts.annotations.AuthenticationType;
import nl.runnable.alfresco.webscripts.annotations.Cache;
import nl.runnable.alfresco.webscripts.annotations.HttpMethod;
import nl.runnable.alfresco.webscripts.annotations.RequestParam;
import nl.runnable.alfresco.webscripts.annotations.Uri;
import nl.runnable.alfresco.webscripts.annotations.UriVariable;
import nl.runnable.alfresco.webscripts.annotations.WebScript;

import org.eclipse.gemini.blueprint.context.BundleContextAware;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.launch.Framework;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * Web Script for the Dynamic Extensions console.
 * 
 * @author Laurens Fridael
 * 
 */
@ManagedBean
@WebScript
@Authentication(AuthenticationType.ADMIN)
@Cache(neverCache = true)
public class ControlPanel implements BundleContextAware {

	private static final int FRAMEWORK_BUNDLE_ID = 0;

	/* Dependencies */

	@Inject
	private Configuration configuration;

	private BundleContext bundleContext;

	@Inject
	private BundleHelper bundleHelper;

	/* Main operations */

	/**
	 * Generates reference data for the index page.
	 * 
	 * @return The model.
	 */
	@Uri(method = HttpMethod.GET, value = "/dynamic-extensions/", defaultFormat = "html")
	public Map<String, Object> index() {
		final Map<String, Object> model = new HashMap<String, Object>();
		model.put(Variables.FRAMEWORK_BUNDLES, bundleHelper.getFrameworkBundles());
		model.put(Variables.EXTENSION_BUNDLES, bundleHelper.getExtensionBundles());
		model.put(Variables.FILE_INSTALL_PATHS, configuration.getFileInstallPaths());
		return model;
	}

	/**
	 * Restarts the {@link Bundle} with the given ID.
	 * 
	 * @param wait
	 * @param response
	 * @throws IOException
	 * @throws BundleException
	 */
	@Uri(method = HttpMethod.POST, value = "/dynamic-extensions/framework/restart")
	public void restartFramework(@RequestParam(defaultValue = "0") final long wait,
			@Attribute final ResponseHelper response) throws IOException, BundleException {
		restartFramework((Framework) bundleContext.getBundle(FRAMEWORK_BUNDLE_ID));
		response.status(HttpServletResponse.SC_OK, "Restarted framework.");
	}

	@Uri(method = HttpMethod.GET, value = "/dynamic-extensions/bundles/{id}", defaultFormat = "html")
	public Map<String, Object> bundle(@UriVariable final long id, @Attribute final ResponseHelper responseHelper)
			throws IOException {
		final Map<String, Object> model = new HashMap<String, Object>();
		final Bundle bundle = bundleContext.getBundle(id);
		if (bundle != null) {
			model.put(Variables.BUNDLE, new TemplateBundle(bundle));
		} else {
			model.put(Variables.ID, id);
			responseHelper.status(HttpServletResponse.SC_NOT_FOUND);
		}
		return model;
	}

	/* Attributes */

	@Attribute
	protected ResponseHelper getResponseHelper(final WebScriptRequest request, final WebScriptResponse response) {
		return new ResponseHelper(request, response);
	}

	/* Utility operations */

	protected void restartFramework(final Framework framework) throws IOException {
		try {
			framework.stop();
			framework.waitForStop(0);
			framework.start();
		} catch (final BundleException e) {
		} catch (final InterruptedException e) {
		}
	}

	/* Dependencies */

	@Override
	public void setBundleContext(final BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

}
