package nl.runnable.alfresco.extensions.controlpanel;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import nl.runnable.alfresco.extensions.controlpanel.template.Variables;
import nl.runnable.alfresco.osgi.SystemPackage;
import nl.runnable.alfresco.webscripts.annotations.Attribute;
import nl.runnable.alfresco.webscripts.annotations.Before;
import nl.runnable.alfresco.webscripts.annotations.Uri;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * Abstract base class for the Control Panel. This class defines common dependencies and {@link Attribute} and
 * {@link Before} handlers.
 * 
 * @author Laurens Fridael
 * 
 */
abstract class AbstractControlPanelHandler {

	private static final String[] FLASH_VARIABLES = new String[] { Variables.INSTALLED_BUNDLE,
			Variables.SUCCESS_MESSAGE, Variables.ERROR_MESSAGE };

	@Inject
	protected nl.runnable.alfresco.osgi.Configuration osgiConfiguration;

	@Inject
	@Named("osgi.container.SystemPackages")
	private List<SystemPackage> systemPackages;

	/* Attributes */

	@Attribute
	protected ResponseHelper getResponseHelper(final WebScriptRequest request, final WebScriptResponse response) {
		return new ResponseHelper(request, response);
	}

	@Attribute(Variables.CONFIGURATION)
	protected nl.runnable.alfresco.osgi.Configuration getConfiguration() {
		return osgiConfiguration;
	}

	@Attribute(Variables.CURRENT_USER)
	protected String getCurrentUser() {
		return AuthenticationUtil.getFullyAuthenticatedUser();
	}

	/* Before handlers */

	@Before
	protected void addFlashMessagesToModel(final @Attribute ResponseHelper responseHelper,
			final Map<String, Object> model) {
		for (final String variable : FLASH_VARIABLES) {
			model.put(variable, responseHelper.getFlashVariable(variable));
		}
	}

	/* Utility operations */

	@SuppressWarnings("unchecked")
	protected Collection<SystemPackage> getSystemPackages() {
		return (Collection<SystemPackage>) systemPackages.get(0);
	}

	/**
	 * Constructs a {@link Map} with a single value. This is useful for {@link Uri} handlers that contribute only one
	 * value to a model.
	 * 
	 * @param name
	 * @param value
	 * @return
	 */
	protected Map<String, Object> model(final String name, final Object value) {
		final HashMap<String, Object> model = new HashMap<String, Object>();
		model.put(name, value);
		return model;
	}

}
