package nl.runnable.alfresco.webscripts;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.ManagedBean;

import nl.runnable.alfresco.webscripts.annotations.Cache;
import nl.runnable.alfresco.webscripts.annotations.ResponseTemplate;
import nl.runnable.alfresco.webscripts.annotations.Uri;
import nl.runnable.alfresco.webscripts.annotations.WebScript;

@ManagedBean
@WebScript(defaultFormat = "html")
@Cache(neverCache = true)
public class ResponseTemplateHandler {

	@Uri("/handleResponseTemplate")
	public Map<String, Object> handleResponseTemplate() {
		return new HashMap<String, Object>();
	}

	@Uri("/handleResponseTemplateWithAnnotation")
	@ResponseTemplate
	public void handleResponseTemplateWithAnnotation() {
	}

	@Uri("/handleResponseTemplateWithCustomName")
	@ResponseTemplate("custom-template.html")
	public void handleResponseTemplateWithCustomName() {
	}

}
