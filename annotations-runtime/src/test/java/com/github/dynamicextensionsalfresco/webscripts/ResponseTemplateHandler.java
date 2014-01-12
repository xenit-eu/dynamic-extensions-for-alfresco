package com.github.dynamicextensionsalfresco.webscripts;

import com.github.dynamicextensionsalfresco.spring.Spied;
import com.github.dynamicextensionsalfresco.webscripts.annotations.Cache;
import com.github.dynamicextensionsalfresco.webscripts.annotations.ResponseTemplate;
import com.github.dynamicextensionsalfresco.webscripts.annotations.Uri;
import com.github.dynamicextensionsalfresco.webscripts.annotations.WebScript;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

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

	@Uri("handleResponseTemplateWithReturnValue")
	public String handleResponseTemplateWithReturnValue() {
		return "custom-returned-template.html";
	}

	@Uri("handleResponseTemplateWithReturnValueOverride")
	@ResponseTemplate("default-template.html")
	public String handleResponseTemplateWithReturnValueOverride() {
		return "custom-returned-template.html";
	}

	@Uri("handleResponseTemplateWithReturnValueDefault")
	@ResponseTemplate("default-template.html")
	public String handleResponseTemplateWithReturnValueDefault() {
		return null;
	}
}
