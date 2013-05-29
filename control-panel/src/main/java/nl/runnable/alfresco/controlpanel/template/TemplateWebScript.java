package nl.runnable.alfresco.controlpanel.template;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import nl.runnable.alfresco.webscripts.AnnotationWebScript;

import org.springframework.extensions.webscripts.WebScript;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public class TemplateWebScript {

	private final WebScript webScript;

	public TemplateWebScript(final WebScript webScript) {
		Assert.notNull(webScript);
		this.webScript = webScript;
	}

	public String getMethod() {
		return webScript.getDescription().getMethod();
	}

	public List<String> getUris() {
		return Arrays.asList(webScript.getDescription().getURIs());
	}

	public String getHandler() {
		if (webScript instanceof AnnotationWebScript) {
			final Method uriMethod = ((AnnotationWebScript) webScript).getHandlerMethods().getUriMethod();
			return ClassUtils.getQualifiedMethodName(uriMethod);
		} else {
			return null;
		}
	}
}
