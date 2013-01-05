package nl.runnable.alfresco.webscripts;

import org.springframework.extensions.webscripts.TemplateProcessorRegistry;

/**
 * Proxy to the TemplateProcessorRegistry which we need to reset the template cache after deploying bundles.
 * @author Laurent Van der Linden
 */
public interface TemplateProcessorRegistryHelper {
    TemplateProcessorRegistry getTemplateProcessorRegistry();
}
