package com.github.dynamicextensionsalfresco.controlpanel;

import com.github.dynamicextensionsalfresco.controlpanel.template.Variables;
import com.github.dynamicextensionsalfresco.osgi.ConfigurationValues;
import com.github.dynamicextensionsalfresco.osgi.SystemPackage;
import com.github.dynamicextensionsalfresco.webscripts.annotations.Attribute;
import com.github.dynamicextensionsalfresco.webscripts.annotations.Before;
import com.github.dynamicextensionsalfresco.webscripts.annotations.ExceptionHandler;
import com.github.dynamicextensionsalfresco.webscripts.annotations.Uri;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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

	@Autowired
	protected com.github.dynamicextensionsalfresco.osgi.Configuration osgiConfiguration;

	@Resource(name = "osgi.container.SystemPackages")
	private ConfigurationValues<SystemPackage> systemPackages;

	/* Attributes */

	@Attribute
	protected ResponseHelper getResponseHelper(final WebScriptRequest request, final WebScriptResponse response) {
		return new ResponseHelper(request, response, osgiConfiguration);
	}

	@Attribute(Variables.CONFIGURATION)
	protected com.github.dynamicextensionsalfresco.osgi.Configuration getConfiguration() {
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

	/* Exception handling */

	@ExceptionHandler(WebScriptException.class)
	protected void handleWebscriptException(WebScriptException wx, @Attribute ResponseHelper responseHelper) throws IOException {
		responseHelper.flashErrorMessage(wx.getMessage(), wx);
	}

	/* Utility operations */

	protected Collection<SystemPackage> getSystemPackages() {
		return systemPackages.getValues();
	}

	/**
	 * Constructs a {@link Map} with a single value. This is useful for {@link Uri} handlers that contribute only one
	 * value to a model.
	 */
	protected Map<String, Object> model(final String name, final Object value) {
		final HashMap<String, Object> model = new HashMap<String, Object>();
		model.put(name, value);
		return model;
	}
}
