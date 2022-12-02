package com.github.dynamicextensionsalfresco.web;

import com.github.dynamicextensionsalfresco.webscripts.WebScriptUriRegistry;
import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.extensions.webscripts.WebScript;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.validation.constraints.NotNull;
import java.io.IOException;

/**
 * Register resources webscript if /META-INF/alfresco/web resources are found
 *
 * @author Laurent Van der Linden
 */
public final class WebResourcesRegistrar implements ResourceLoaderAware {

    @NotNull
    private static final String WEB_PATH = "/META-INF/alfresco/web/";

    @Autowired
    @Nullable
    private WebScriptUriRegistry webscriptRegistry;
    @Autowired
    @Nullable
    private BundleContext bundleContext;
    private ResourcePatternResolver resourcePatternResolver;
    private WebScript currentWebscript;

    @Nullable
    protected final WebScriptUriRegistry getWebscriptRegistry() {
        return this.webscriptRegistry;
    }

    protected final void setWebscriptRegistry(@Nullable WebScriptUriRegistry webscriptRegistry) {
        this.webscriptRegistry = webscriptRegistry;
    }

    @Nullable
    protected final BundleContext getBundleContext() {
        return this.bundleContext;
    }

    protected final void setBundleContext(@Nullable BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @PostConstruct
    public final void registerResourceWebscript() throws IOException {
        if (resourcePatternResolver == null) {
            return;
        }
        final Resource[] resources = resourcePatternResolver.getResources("osgibundle:" + WEB_PATH + "**");
        if (resources.length == 0) {
            return;
        }

        if (bundleContext == null) {
            throw new IllegalStateException("bundleContext");
        }
        if (webscriptRegistry == null) {
            throw new IllegalStateException("webscriptRegistry");
        }

        currentWebscript = new ResourceWebscript(bundleContext);
        webscriptRegistry.registerWebScript(currentWebscript);
    }

    @PreDestroy
    public final void unregisterResourceWebscript() {
        if (this.currentWebscript != null) {
            if (this.webscriptRegistry == null) {
                throw new IllegalStateException("webscriptRegistry");
            }
            this.webscriptRegistry.unregisterWebScript(this.currentWebscript);
        }

    }

    public void setResourceLoader(@Nullable ResourceLoader resourceLoader) {
        if (resourceLoader == null) {
            throw new IllegalArgumentException(
                    "null cannot be cast to non-null type org.springframework.core.io.support.ResourcePatternResolver");
        } else {
            this.resourcePatternResolver = (ResourcePatternResolver) resourceLoader;
        }
    }
}
