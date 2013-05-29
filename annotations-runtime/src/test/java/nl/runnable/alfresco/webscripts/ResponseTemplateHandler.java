package nl.runnable.alfresco.webscripts;

import java.util.HashMap;
import java.util.Map;

import nl.runnable.alfresco.spring.Spied;
import nl.runnable.alfresco.webscripts.annotations.Cache;
import nl.runnable.alfresco.webscripts.annotations.ResponseTemplate;
import nl.runnable.alfresco.webscripts.annotations.Uri;
import nl.runnable.alfresco.webscripts.annotations.WebScript;

import org.springframework.stereotype.Component;

@Component
@WebScript(defaultFormat = "html")
@Spied
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
