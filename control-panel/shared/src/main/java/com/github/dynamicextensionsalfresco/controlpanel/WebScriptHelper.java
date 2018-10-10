package com.github.dynamicextensionsalfresco.controlpanel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.dynamicextensionsalfresco.controlpanel.template.TemplateWebScript;
import com.github.dynamicextensionsalfresco.webscripts.WebScriptUriRegistry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.extensions.webscripts.WebScript;
import org.springframework.stereotype.Component;

import static java.util.Arrays.asList;

@Component
public class WebScriptHelper {

	@Autowired
	private BundleHelper bundleHelper;

	/* Main operations */

	public Map<String,List<TemplateWebScript>> getWebScripts() {
		final WebScriptUriRegistry registry = getApplicationContextBean(WebScriptUriRegistry.class);
		if (registry != null) {
			final List<WebScript> webScripts = registry.getWebScripts();
			final List<TemplateWebScript> templateWebScripts = new ArrayList<TemplateWebScript>(webScripts.size());
			for (final WebScript webScript : webScripts) {
				templateWebScripts.add(new TemplateWebScript(webScript));
			}
			Map<String,List<TemplateWebScript>> byFamily = new HashMap<String, List<TemplateWebScript>>();
			for (TemplateWebScript templateWebScript : templateWebScripts) {
				Set<String> familys = templateWebScript.getFamilys();
				if (familys == null) {
					familys = new HashSet<String>(asList("no family"));
				}
				for (String family : familys) {
					List<TemplateWebScript> familiyList = byFamily.get(family);
					if (familiyList == null) {
						familiyList = new ArrayList<TemplateWebScript>();
						byFamily.put(family, familiyList);
					}
					familiyList.add(templateWebScript);
				}
			}
			return byFamily;
		} else {
			return Collections.emptyMap();
		}
	}

	/* Utility operations */

	protected <T> T getApplicationContextBean(final Class<T> clazz) {
		final ApplicationContext applicationContext = bundleHelper.getService(ApplicationContext.class);
		if (applicationContext != null) {
			return applicationContext.getBean(clazz);
		} else {
			return null;
		}
	}

}
