package nl.runnable.alfresco.controlpanel.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nl.runnable.alfresco.annotations.OsgiService;
import nl.runnable.alfresco.controlpanel.BundleService;
import nl.runnable.alfresco.controlpanel.WebScriptService;
import nl.runnable.alfresco.controlpanel.template.TemplateWebScript;
import nl.runnable.alfresco.webscripts.WebScriptUriRegistry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.extensions.webscripts.WebScript;
import org.springframework.stereotype.Component;

@Component
@OsgiService
public class WebScriptServiceImpl implements WebScriptService {

	@Autowired
	private BundleService bundleService;

	/* Main operations */

	/* (non-Javadoc)
	 * @see nl.runnable.alfresco.controlpanel.WebScriptService#getWebScripts()
	 */
	@Override
	public List<TemplateWebScript> getWebScripts() {
		final WebScriptUriRegistry registry = getApplicationContextBean(WebScriptUriRegistry.class);
		if (registry != null) {
			final List<WebScript> webScripts = registry.getWebScripts();
			final List<TemplateWebScript> templateWebScripts = new ArrayList<TemplateWebScript>(webScripts.size());
			for (final WebScript webScript : webScripts) {
				templateWebScripts.add(new TemplateWebScript(webScript));
			}
			return templateWebScripts;
		} else {
			return Collections.emptyList();
		}
	}

	/* Utility operations */

	protected <T> T getApplicationContextBean(final Class<T> clazz) {
		final ApplicationContext applicationContext = bundleService.getService(ApplicationContext.class);
		if (applicationContext != null) {
			return applicationContext.getBean(clazz);
		} else {
			return null;
		}
	}

}
