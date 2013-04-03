package nl.runnable.alfresco.webscripts;

import java.lang.annotation.Annotation;

import nl.runnable.alfresco.webscripts.annotations.FileField;
import nl.runnable.alfresco.webscripts.annotations.Header;

import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.extensions.webscripts.servlet.FormData.FormField;
import org.springframework.extensions.webscripts.servlet.WebScriptServletRequest;
import org.springframework.util.StringUtils;

/**
 * {@link ArgumentResolver} that maps String parameters annotated with {@link Header}.
 * 
 * @author Laurens Fridael
 * 
 */
public class FileFieldArgumentResolver implements ArgumentResolver<FormField, FileField> {

	@Override
	public boolean supports(final Class<?> argumentType, final Class<? extends Annotation> annotationType) {
		return FormField.class.equals(argumentType) && FileField.class.equals(annotationType);
	}

	@Override
	public FormField resolveArgument(final Class<?> argumentType, final FileField fileField, final String name,
			WebScriptRequest request, final WebScriptResponse response) {
		if (request instanceof AttributesWebScriptRequest) {
			request = ((AttributesWebScriptRequest) request).getWebScriptRequest();
		}
		FormField formField = null;
		final WebScriptServletRequest req = WebScriptUtil.extractWebScriptServletRequest(request);
		if (req != null) {
			String parameterName = fileField.value();
			if (StringUtils.hasText(parameterName) == false) {
				parameterName = name;
			}
			formField = req.getFileField(parameterName);
		}
		return formField;
	}
}
