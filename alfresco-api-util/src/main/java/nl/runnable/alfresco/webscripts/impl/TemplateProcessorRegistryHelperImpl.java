package nl.runnable.alfresco.webscripts.impl;

import nl.runnable.alfresco.webscripts.TemplateProcessorRegistryHelper;
import org.springframework.extensions.webscripts.TemplateProcessorRegistry;

/**
 * @author Laurent Van der Linden
 */
public class TemplateProcessorRegistryHelperImpl implements TemplateProcessorRegistryHelper {
    private TemplateProcessorRegistry templateProcessorRegistry;

    @Override
    public TemplateProcessorRegistry getTemplateProcessorRegistry() {
        return templateProcessorRegistry;
    }

    public void setTemplateProcessorRegistry(final TemplateProcessorRegistry templateProcessorRegistry) {
        this.templateProcessorRegistry = templateProcessorRegistry;
    }
}
