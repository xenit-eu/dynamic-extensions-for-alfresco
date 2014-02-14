package com.github.dynamicextensionsalfresco.webscripts.resolutions;

import com.github.dynamicextensionsalfresco.webscripts.AnnotationWebScriptRequest;
import com.github.dynamicextensionsalfresco.webscripts.AnnotationWebscriptResponse;
import com.github.dynamicextensionsalfresco.webscripts.UrlModel;
import org.springframework.extensions.webscripts.Format;
import org.springframework.extensions.webscripts.TemplateProcessor;
import org.springframework.extensions.webscripts.TemplateProcessorRegistry;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author Laurent Van der Linden
 */
public class TemplateResolution extends AbstractResolution {
    private static final String URL_VARIABLE = "url";
    private static final String WEBSCRIPT_VARIABLE = "webscript";

    private String template;
    private Map<String,Object> model;

    public TemplateResolution(Map<String, Object> model) {
        this.model = model;
    }

    public TemplateResolution(String template, Map<String, Object> model) {
        this.template = template;
        this.model = model;
    }

    public TemplateResolution(String template) {
        this.template = template;
    }

    @Override
    public void resolve(AnnotationWebScriptRequest request, AnnotationWebscriptResponse response, ResolutionParameters params) throws IOException {
        if (StringUtils.hasText(request.getFormat()) == false) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("No format specified.");
            return;
        }
        populateTemplateModel(model, request, params);
        int status = response.getStatus() != null ? response.getStatus() : 200;
        processTemplate(request, model, status, response, params);
    }

    protected void populateTemplateModel(Map<String, Object> model, AnnotationWebScriptRequest request, ResolutionParameters params) {
        model.put(WEBSCRIPT_VARIABLE, params.getDescription());
        model.put(URL_VARIABLE, new UrlModel(request));
    }

    protected TemplateProcessor getTemplateProcessor(AnnotationWebScriptRequest request) {
        final TemplateProcessorRegistry templateProcessorRegistry = request.getRuntime().getContainer()
            .getTemplateProcessorRegistry();
        return templateProcessorRegistry.getTemplateProcessorByExtension("ftl");
    }

    protected String generateTemplateName(final TemplateProcessor templateProcessor, final String format,
                                          final int status, ResolutionParameters parameters) {
        final Class<?> handlerClass = parameters.getHandlerClass();
        final String methodName = parameters.getUriMethod().getName();
        final String httpMethod = parameters.getDescription().getMethod().toLowerCase();

        final String baseTemplateName = String.format("%s.%s.%s",
            ClassUtils.getQualifiedName(handlerClass).replace('.', '/'), methodName, httpMethod);
		/* <java class + method>.<http method>.<format>.ftl */
        final String defaultTemplateName = String.format("%s.%s.ftl", baseTemplateName, format.toLowerCase());

		/* <java class + method>.<http method>.<format>.<status>.ftl */
        String templateName = String.format("%s.%s.%d.ftl", baseTemplateName, format.toLowerCase(), status);
        if (templateProcessor.hasTemplate(templateName) == false) {
            final String packageName = handlerClass.getPackage().getName().replace('.', '/');
			/* <java package>.<format>.<status>.ftl */
            templateName = String.format("%s/%s.%d.ftl", packageName, format.toLowerCase(), status);
        }
        if (templateProcessor.hasTemplate(templateName) == false) {
			/* <format>.<status>.ftl */
            templateName = String.format("%s.%d.ftl", format, status);
        }
        if (templateProcessor.hasTemplate(templateName) == false) {
            templateName = defaultTemplateName;
        }
        return templateName;
    }

    protected void processTemplate(final AnnotationWebScriptRequest request, final Map<String, Object> model, final int status,
                                   final WebScriptResponse response, ResolutionParameters params) throws IOException {
        final TemplateProcessor templateProcessor = getTemplateProcessor(request);
        final String format = request.getFormat();
        String templateName = getTemplate();
        if (StringUtils.hasText(templateName) == false) {
            templateName = generateTemplateName(templateProcessor, format, status, params);
        }
        if (templateProcessor.hasTemplate(templateName)) {
            response.setContentType(Format.valueOf(format.toUpperCase()).mimetype());
            response.setContentEncoding("utf-8");
            addCacheControlHeaders(response, params);
            templateProcessor.process(templateName, model, response.getWriter());
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write(String.format("Could not find template: %s", templateName));
        }
    }

    public Map<String, Object> getModel() {
        return model;
    }

    public void setModel(Map<String, Object> model) {
        this.model = model;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }
}
