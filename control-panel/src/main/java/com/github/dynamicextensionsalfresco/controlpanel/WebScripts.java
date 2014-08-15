package com.github.dynamicextensionsalfresco.controlpanel;

import java.util.Map;

import com.github.dynamicextensionsalfresco.controlpanel.template.Variables;
import com.github.dynamicextensionsalfresco.webscripts.annotations.HttpMethod;
import com.github.dynamicextensionsalfresco.webscripts.annotations.ResponseTemplate;
import com.github.dynamicextensionsalfresco.webscripts.annotations.Uri;
import com.github.dynamicextensionsalfresco.webscripts.annotations.WebScript;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@WebScript(baseUri = "/dynamic-extensions/web-scripts", defaultFormat = "html", families = "control panel")
public class WebScripts extends AbstractControlPanelHandler {

	@Autowired
	WebScriptHelper webScriptHelper;

	@Uri(method = HttpMethod.GET)
	@ResponseTemplate
	public void index(final Map<String, Object> model) {
		model.put(Variables.WEB_SCRIPTS, webScriptHelper.getWebScripts());
	}
}
