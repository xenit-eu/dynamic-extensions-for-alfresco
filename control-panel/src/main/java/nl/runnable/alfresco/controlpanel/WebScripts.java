package nl.runnable.alfresco.controlpanel;

import java.util.Map;

import nl.runnable.alfresco.controlpanel.template.Variables;
import nl.runnable.alfresco.webscripts.annotations.HttpMethod;
import nl.runnable.alfresco.webscripts.annotations.ResponseTemplate;
import nl.runnable.alfresco.webscripts.annotations.Uri;
import nl.runnable.alfresco.webscripts.annotations.WebScript;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@WebScript(baseUri = "/dynamic-extensions/web-scripts", defaultFormat = "html")
public class WebScripts extends AbstractControlPanelHandler {

	@Autowired
	WebScriptService webScriptService;

	@Uri(method = HttpMethod.GET)
	@ResponseTemplate
	public void index(final Map<String, Object> model) {
		model.put(Variables.WEB_SCRIPTS, webScriptService.getWebScripts());
	}
}
