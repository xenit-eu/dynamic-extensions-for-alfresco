package com.github.dynamicextensionsalfresco.web

import com.github.dynamicextensionsalfresco.webscripts.WebScriptUriRegistry
import org.osgi.framework.BundleContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ResourceLoaderAware
import org.springframework.core.io.ResourceLoader
import org.springframework.core.io.support.ResourcePatternResolver
import org.springframework.extensions.webscripts.WebScript
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

/**
 *  Register resources webscript if /META-INF/alfresco/web resources are found
 *
 * @author Laurent Van der Linden
 */
public class WebResourcesRegistrar constructor() :ResourceLoaderAware {

    @Autowired
    protected var webscriptRegistry: WebScriptUriRegistry? = null;

    @Autowired
    protected var bundleContext: BundleContext? = null;

    private var resourcePatternResolver: ResourcePatternResolver? = null

    private var currentWebscript: WebScript? = null

    @PostConstruct
    fun registerResourceWebscript() {
        if (resourcePatternResolver?.getResources("osgibundle:$WEB_PATH**")?.isNotEmpty() ?: false) {
            currentWebscript = ResourceWebscript(bundleContext!!)
            webscriptRegistry!!.registerWebScript(currentWebscript)
        }
    }

    @PreDestroy
    fun unregisterResourceWebscript() {
        if (currentWebscript != null) {
            webscriptRegistry!!.unregisterWebScript(currentWebscript)
        }
    }

    override fun setResourceLoader(resourceLoader: ResourceLoader?) {
        this.resourcePatternResolver = resourceLoader as ResourcePatternResolver
    }

    companion object {
        val WEB_PATH = "/META-INF/alfresco/web/"
    }
}